package im.actor.server.search.index

import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.analyzers._

import scala.concurrent.{ ExecutionContext, Future }

trait ElasticSearchIndexer {

  val client: ElasticClient
  val indexName: String
  implicit val ec: ExecutionContext

  def create(): Future[Unit] = {
    for {
      exists ← client.execute(index.exists(indexName)).map(_.isExists)
      _ ← if (!exists) {
        client.execute {
          createIndex(indexName).shards(4).mappings(
            mapping("messages").fields(
              longField("randomId"),
              longField("ts"),
              intField("senderId"),
              intField("peerType"),
              intField("peerId"),
              intField("contentType"),
              multiField("content").as(
                stringField("raw").analyzer(StandardAnalyzer),
                stringField("arabic").analyzer(ArabicLanguageAnalyzer),
                stringField("chinese").analyzer(ChineseLanguageAnalyzer),
                stringField("english").analyzer(EnglishLanguageAnalyzer),
                stringField("french").analyzer(FrenchLanguageAnalyzer),
                stringField("german").analyzer(GermanLanguageAnalyzer),
                stringField("portuguese").analyzer(PortugueseLanguageAnalyzer),
                stringField("russian").analyzer(RussianLanguageAnalyzer),
                stringField("spanish").analyzer(SpanishLanguageAnalyzer)
              ),
              booleanField("isPublic"),
              intField("users")
            )
          )
        } map (_.isAcknowledged)
      } else Future.successful(())
    } yield ()
  }

  // TODO: add failure logging
  def indexMessages(messages: Seq[IndexedMessage]): Future[Unit] = {
    if (messages.isEmpty) {
      Future.successful(())
    } else {
      client.execute {
        bulk {
          messages.map { mess ⇒ indexInto(indexName / "messages").source(mess) }
        }
      }.map(_ ⇒ ())
    }
  }

}
