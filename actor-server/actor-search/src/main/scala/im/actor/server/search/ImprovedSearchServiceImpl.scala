package im.actor.server.search

import akka.actor.ActorSystem
import cats.data.Xor
import im.actor.api.rpc.ClientData
import im.actor.api.rpc._
import im.actor.api.rpc.search.{ ApiSearchCondition, ResponseMessageSearchResponse }
import im.actor.api.rpc.sequence.ApiUpdateOptimization
import im.actor.server.api.rpc.service.search.SearchServiceImpl
import im.actor.server.search.SearchErrors.SearchError

import scala.concurrent.Future

object SearchServiceErrors {
  val InvalidLoadState = RpcError(400, "INVALID_LOAD_STATE", "", false, None)
  def invalodSearchCondition(message: String) = RpcError(400, "INVALID_SEARCH_CONDITION", message, false, None)

  def convertToRpcError: PartialFunction[SearchError, RpcError] = {
    case SearchErrors.InvalidLoadState            ⇒ InvalidLoadState
    case SearchErrors.InvalidSearchCondition(msg) ⇒ invalodSearchCondition(msg)
  }
}

final class ImprovedSearchServiceImpl(implicit val system: ActorSystem) extends SearchServiceImpl with SearchConditionHelpers {

  override def doHandleMessageSearch(query: ApiSearchCondition, optimizations: IndexedSeq[ApiUpdateOptimization.Value], clientData: ClientData): Future[HandlerResult[ResponseMessageSearchResponse]] = {
    authorized(clientData) { implicit client ⇒
      searchResult(query, client.userId, client.authId) map {
        case Xor.Right(res) ⇒ Ok(res)
        case Xor.Left(err)  ⇒ Error(SearchServiceErrors.convertToRpcError(err))
      }
    }
  }

  override def doHandleMessageSearchMore(loadMoreState: Array[Byte], optimizations: IndexedSeq[ApiUpdateOptimization.Value], clientData: ClientData): Future[HandlerResult[ResponseMessageSearchResponse]] = {
    authorized(clientData) { implicit client ⇒
      searchMoreResult(loadMoreState, client.userId, client.authId) map {
        case Xor.Right(res) ⇒ Ok(res)
        case Xor.Left(err)  ⇒ Error(SearchServiceErrors.convertToRpcError(err))
      }
    }

  }
}
