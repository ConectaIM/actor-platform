package im.actor.server.search

import akka.actor.ActorSystem
import akka.event.Logging
import cats.data.Xor
import com.google.protobuf.ByteString
import com.sksamuel.elastic4s.ElasticDsl._
import com.sksamuel.elastic4s._
import im.actor.api.rpc.PeersImplicits
import im.actor.api.rpc.messaging.{ ApiMessage, ApiTextMessage }
import im.actor.api.rpc.peers.{ ApiPeer, ApiPeerType }
import im.actor.api.rpc.search._
import im.actor.concurrent.FutureResult
import im.actor.server.group.GroupUtils
import im.actor.server.messaging.MessageParsing
import im.actor.server.model.Peer
import im.actor.server.model.PeerType._
import im.actor.server.persist.HistoryMessageRepo
import im.actor.server.search.SearchErrors.SearchError
import im.actor.server.search.index.IndexedMessage
import im.actor.util.log.AnyRefLogSource
import org.elasticsearch.index.query.MatchQueryBuilder
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.BEST_FIELDS
import org.elasticsearch.search.sort.SortOrder.DESC
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.control.NoStackTrace

object SearchErrors {
  sealed abstract class SearchError(msg: String) extends RuntimeException(msg) with NoStackTrace
  case object InvalidLoadState extends SearchError("Invalid loadMoreState")
  final case class InvalidSearchCondition(msg: String) extends SearchError(s"Invalid search condition, reason: $msg")
}

trait SearchConditionHelpers extends PeersImplicits
  with LanguageFieldDetection
  with MessageParsing
  with FutureResult[SearchError]
  with AnyRefLogSource {
  import SearchErrors._
  import models._

  protected implicit val system: ActorSystem
  protected implicit val ec: ExecutionContext
  protected val db: Database

  private val log = Logging(system, this)
  private lazy val clientExt = SearchClientExtension(system)

  def searchResult(cond: ApiSearchCondition, clientId: Int, clientAuthId: Long): Future[SearchError Xor ResponseMessageSearchResponse] =
    (for {
      _ ← fromXor(validateCondition(cond) leftMap (reason ⇒ InvalidSearchCondition(reason)))
      langFields ← fromFuture(detectContentFields(cond, clientId))
      result ← fromFuture(messagesSearch(cond, None, clientId, clientAuthId, langFields))
    } yield result).value

  def searchMoreResult(loadMoreState: Array[Byte], clientId: Int, clientAuthId: Long): Future[SearchError Xor ResponseMessageSearchResponse] = {
    (for {
      loadState ← fromOption(InvalidLoadState)(LoadMoreState.validate(loadMoreState).toOption)
      LoadMoreState(ts, condBytes, langFields) = loadState
      cond ← fromXor(Xor.fromEither(ApiSearchCondition.parseFrom(condBytes)) leftMap (_ ⇒ InvalidSearchCondition("Broken search condition")))
      result ← fromFuture(messagesSearch(cond, Some(ts), clientId, clientAuthId, langFields.toVector))
    } yield result).value
  }

  private def messagesSearch(cond: ApiSearchCondition, optTs: Option[Long], clientId: Int, clientAuthId: Long, langFields: Vector[String]): Future[ResponseMessageSearchResponse] = {
    val filters = optTs.fold(userQuery(clientId)) { ts ⇒
      bool(
        must(
          userQuery(clientId),
          rangeQuery("ts").lte(ts.toDouble)
        )
      )
    }

    val messageQuery = searchCondition(cond, clientId)(clientExt.searchQueryConfig, langFields) match {
      case Left(qry) ⇒
        must(qry).filter(filters)
      case Right(flt) ⇒
        filter(flt, filters)
    }

    val searchQuery =
      search in clientExt.indexerConfig.indexName / "messages" query messageQuery sort (
        scoreSort order DESC,
        field sort "ts" order DESC
      ) size 30
    log.debug("Search query: {}", searchQuery)
    for {
      searchResp ← clientExt.client execute searchQuery
      _ = log.debug("Search request: {}, response: {}", cond, searchResp)
      (optLastTs, searchItems) = extractMessages(searchResp.as[IndexedMessage])
      found ← searchItems
      (groupIds, userIds) = found.view.map(_.result.peer).foldLeft(Vector.empty[Int], Vector.empty[Int]) {
        case ((gids, uids), ApiPeer(pt, pid)) ⇒
          pt match {
            case ApiPeerType.Private ⇒ (gids, uids :+ pid)
            case ApiPeerType.Group   ⇒ (gids :+ pid, uids)
          }
      }
      (groups, users) ← GroupUtils.getGroupsUsers(groupIds, userIds, clientId, clientAuthId)
      optLoadMore = optLastTs map { lastTs ⇒ LoadMoreState(lastTs, ByteString.copyFrom(cond.toByteArray), langFields).toByteArray }
    } yield ResponseMessageSearchResponse(found, users.toVector, groups.toVector, optLoadMore, Vector.empty, Vector.empty)
  }

  private def extractMessages(messages: Array[IndexedMessage]) = {
    val acc = Option.empty[Long] → Future.successful(Vector.empty[ApiMessageSearchItem])
    (messages foldLeft acc) {
      case ((_, messFu), mess) ⇒
        log.debug("Parsed messages from search: {}", mess)
        val searchResults = for {
          messages ← messFu
          optItem ← db.run(HistoryMessageRepo.findBySender(mess.senderId, mess.peer, mess.randomId).headOption) map { optHm ⇒
            for {
              hm ← optHm
              message ← Xor.fromEither(parseMessage(hm.messageContentData)).toOption
            } yield apiSearchItem(mess, message)
          }
        } yield optItem match {
          case Some(item) ⇒ messages :+ item
          case None       ⇒ messages
        }
        Some(mess.ts) → searchResults
    }
  }

  private def apiSearchItem(mess: IndexedMessage, apiMess: ApiMessage) =
    ApiMessageSearchItem(
      ApiMessageSearchResult(
        peer = mess.peer.asStruct,
        randomId = mess.randomId,
        date = mess.ts,
        senderId = mess.senderId,
        content = adjustContent(apiMess, mess.content)
      )
    )

  //for text messages we may store different content in index(in case of links)
  private def adjustContent(message: ApiMessage, content: String): ApiMessage = message match {
    case m: ApiTextMessage ⇒ m.copy(text = content)
    case _                 ⇒ message
  }

  private def userQuery(clientId: Int) =
    filter(
      termQuery("users", clientId),
      termQuery("isPublic", true)
    )

  private def searchCondition(searchCondition: ApiSearchCondition, clientId: Int)(implicit config: SearchQueryConfig, langFields: Vector[String]): Either[QueryDefinition, QueryDefinition] =
    searchCondition match {
      case ApiSearchPeerCondition(apiPeer) ⇒
        val peer = apiPeer.asModel
        val peerFilter = peer match {
          case Peer(Group, groupId) ⇒
            must(
              termQuery("peerType", Group.value),
              termQuery("peerId", peer.id)
            )
          case Peer(Private, peerUserId) ⇒
            should(
              must(
                termQuery("peerType", Private.value),
                termQuery("peerId", peerUserId),
                termQuery("users", clientId)
              ),
              must(
                termQuery("peerType", Private.value),
                termQuery("peerId", clientId),
                termQuery("users", peerUserId)
              )
            )
        }
        Right(peerFilter)
      case ApiSearchPieceText(text) ⇒
        val contentFields = (langFields map (l ⇒ s"content.$l")) :+ "content.raw"
        Left(bool(
          //        should(
          must(
            multiMatchQuery(text)
              fields contentFields
              matchType BEST_FIELDS
              cutoffFrequency 2.0 //config.cutoffFrequency
              operator MatchQueryBuilder.Operator.AND
              minimumShouldMatch config.minContentMatch
          ) should (matchPhraseQuery("content", text) slop config.sloppiness)
        //          ,must(text.split(" ").filter(_.nonEmpty) map { w ⇒ should(prefixQuery("content.raw", w)) })
        //        )
        ))
      case ApiSearchSenderIdConfition(senderId) ⇒
        Right(termQuery("senderId", senderId))
      case ApiSearchPeerContentType(ct) ⇒
        Right(termQuery("contentType", ct.id))
      case ApiSearchAndCondition(conds) ⇒
        val (qs, fs) = splitQueriesAndFilters(conds, clientId)
        Left(must(qs).filter(fs))
      case ApiSearchOrCondition(conds) ⇒
        val (qs, fs) = splitQueriesAndFilters(conds, clientId)
        Left(should(qs :+ matchAllQuery).filter(fs))
      case ApiSearchPeerTypeCondition(_) ⇒
        Left(matchAllQuery)
    }

  // queries - first, filters - second
  private def splitQueriesAndFilters(conds: IndexedSeq[ApiSearchCondition], clientId: Int)(implicit config: SearchQueryConfig, langFields: Vector[String]): (Vector[QueryDefinition], Vector[QueryDefinition]) = {
    val acc = Vector.empty[QueryDefinition] → Vector.empty[QueryDefinition]
    (conds foldLeft acc) { (acc, el) ⇒
      searchCondition(el, clientId) match {
        case Left(query)   ⇒ acc.copy(_1 = acc._1 :+ query)
        case Right(filter) ⇒ acc.copy(_2 = acc._2 :+ filter)
      }
    }
  }

  private def validateCondition(cond: ApiSearchCondition): String Xor Unit = {
    def validateSubConds(subconds: IndexedSeq[ApiSearchCondition]) =
      (subconds map validateCondition) collectFirst { case x: Xor.Left[String] ⇒ x } getOrElse Xor.Right(())
    cond match {
      case ApiSearchPieceText(text)        ⇒ if (text.nonEmpty) Xor.right(()) else Xor.left("Empty text search condition")
      case ApiSearchOrCondition(subconds)  ⇒ validateSubConds(subconds)
      case ApiSearchAndCondition(subconds) ⇒ validateSubConds(subconds)
      case _                               ⇒ Xor.right(())
    }
  }

}
