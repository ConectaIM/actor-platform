package im.actor.server.search

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.util.FastFuture
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.stage.{ GraphStage, GraphStageLogic, OutHandler, TimerGraphStageLogic }
import com.sksamuel.elastic4s.ElasticClient
import im.actor.api.rpc.messaging.{ ApiDocumentMessage, ApiMessage, ApiTextMessage }
import im.actor.server.db.DbExtension
import im.actor.server.dialog.HistoryUtils
import im.actor.server.messaging.MessageParsing
import im.actor.server.model.{ HistoryMessage, Peer }
import im.actor.server.persist.HistoryMessageRepo
import im.actor.server.search.index.{ ContentType, ElasticSearchIndexer, IndexedMessage }
import org.joda.time.DateTime

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._
import scala.util.{ Failure, Success, Try }

object IndexerStream {
  def apply(client: ElasticClient, indexName: String)(implicit system: ActorSystem, mat: Materializer): IndexerStream = new IndexerStream(client, indexName)
}

final class IndexerStream(val client: ElasticClient, val indexName: String)(implicit system: ActorSystem, mat: Materializer) extends MessageParsing with ElasticSearchIndexer {
  implicit val ec: ExecutionContext = system.dispatcher

  private val log = Logging(system, getClass)

  private val db = DbExtension(system).db

  def run(): Unit = {
    // TODO: rewrite with stream DSL
    Source
      .fromGraph(MessagesSource(MessagesSource.DefaultFetchCount))
      .map { message ⇒
        message → parseMessage(message.messageContentData)
      }
      .alsoTo(errorLoggingSink)
      .collect {
        case (message, Right(parsed)) ⇒
          message → parsed
      }
      .mapAsync(2) {
        case (message, parsed) ⇒
          if (isPublic(message)) {
            FastFuture.successful((message, parsed, Set.empty[Int]))
          } else {
            for (users ← fetchUserIds(message.peer, message.randomId)) yield (message, parsed, users)
          }
      }
      .mapConcat {
        case (message, parsed, userIds) ⇒
          def toIndex(contentType: ContentType, content: String, users: Set[Int]) = IndexedMessage(
            message.randomId,
            message.date.getMillis,
            message.senderUserId,
            message.peer,
            contentType,
            content,
            isPublic(message),
            users
          )
          parsed match {
            case ApiTextMessage(text, _, _) ⇒
              (toIndex(ContentType.Text, text, userIds) +: URLExtractor.extractLinks(text).map { link ⇒
                toIndex(ContentType.Link, link, userIds)
              }).toVector
            case ApiDocumentMessage(_, _, _, name, _, _, _) ⇒
              Vector(toIndex(ContentType.Document, name, userIds)) // TODO: take real content type from extension
            case _ ⇒ Vector.empty // ???
          }
      }
      .groupedWithin(1000, 1.second)
      .mapAsync(2)(bulkIndex) // TODO: write how many messages were indexed
      .runWith(Sink.ignore)
  }

  private def errorLoggingSink = Flow[(HistoryMessage, Either[Any, ApiMessage])].collect {
    case (_, Left(err)) ⇒ err
  }.to(Sink.foreach[Any](e ⇒ log.warning("Failed to parse message")))

  private def isPublic(message: HistoryMessage) = message.userId == HistoryUtils.SharedUserId

  private def fetchUserIds(peer: Peer, randomId: Long) =
    db.run(HistoryMessageRepo.findUserIds(peer, Set(randomId))) map (_.toSet)

  //                            ~> parseLinks  ~>
  // getMessage ~> parseMessage ~> extractText ~> constructMessage ~> group100Messages ~>  indexMessages
  //            ~> getUsers                    ~>

  //  GraphDSL.create() { implicit builder =>
  //    import GraphDSL.Implicits._
  //
  //    val messages = builder.add(MessagesSource(MessagesSource.DefaultFetchCount))
  //
  //    UnzipWith { e: HistoryMessage =>
  //      (e, e, e, e)
  //    }
  //
  //    messages ~>
  //
  //
  //    ClosedShape
  //  }

  private def bulkIndex(messages: Seq[IndexedMessage]): Future[Unit] = indexMessages(messages)

}

object MessagesSource {
  val DefaultFetchCount = 200

  def apply(maxFetchCount: Int)(implicit system: ActorSystem): MessagesSource =
    new MessagesSource(maxFetchCount)(system)
}

final class MessagesSource(maxFetchCount: Int)(system: ActorSystem)
  extends GraphStage[SourceShape[HistoryMessage]] {
  import system.dispatcher
  private val out: Outlet[HistoryMessage] = Outlet("MessagesSourceOut")

  def shape: SourceShape[HistoryMessage] = SourceShape(out)

  def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new TimerGraphStageLogic(shape) {
      private val db = DbExtension(system).db

      private val FetchLater = "fetch"

      private var lastRandomId: Long = 0L
      private var lastDate: DateTime = DateTime.parse("1970-01-01T00:00:00Z")

      private var canFetchMore: Boolean = true

      private[this] var buf: Vector[HistoryMessage] = Vector.empty

      setHandler(out, new OutHandler {
        def onPull(): Unit = deliverBuf()
      })

      override def preStart(): Unit = {
        prefetchMessages()
      }

      override def postStop(): Unit = {
        buf = null
      }

      override def onTimer(timerKey: Any): Unit = timerKey match {
        case FetchLater ⇒
          system.log.debug("Fetch more messages after delay")
          prefetchMessages()
      }

      private def deliverBuf(): Unit = {
        if (buf.length < 20 && canFetchMore) {
          prefetchMessages()
        }
        if (buf.nonEmpty && isAvailable(out)) {
          val head +: tail = buf
          buf = tail
          push(out, head)
        }
      }

      private def prefetchMessages() = {
        canFetchMore = false
        db.run(HistoryMessageRepo.uniqueAsc(lastDate, lastRandomId, maxFetchCount)).onComplete(callback)
      }

      private lazy val callback: Try[Seq[HistoryMessage]] ⇒ Unit =
        getAsyncCallback[Try[Seq[HistoryMessage]]] {
          case Success(messages) ⇒
            messages match {
              case Seq() ⇒ // no new messages, schedule refetch in 30 seconds
                canFetchMore = false
                system.log.debug("No more messages to index, will fetch more after 30 seconds")
                scheduleOnce(FetchLater, 30.seconds)
              case _ :+ last ⇒
                canFetchMore = true
                lastRandomId = last.randomId
                lastDate = last.date
                buf ++= messages
            }
            deliverBuf()
          case Failure(err) ⇒
            system.log.warning("Failed to fetch messages, will retry later")
            canFetchMore = true
            fail(out, err)
        }.invoke(_)
    }

}
