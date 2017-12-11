package im.actor.server.api.rpc.service.grouppre

import java.time.Instant

import akka.actor.ActorSystem
import akka.http.scaladsl.util.FastFuture
import im.actor.api.rpc.grouppre.{ApiGroupPre, GrouppreService, ResponseChangeGroupPre, ResponseLoadGroupsPre}
import im.actor.api.rpc.misc.ResponseSeq
import im.actor.api.rpc.{ClientData, _}
import im.actor.server.grouppre.GroupPreExtension

import scala.concurrent.{ExecutionContext, Future}

/**
 * Created by 98379720172 on 16/11/16.
 */
final class GroupsPreServiceImpl()(implicit actorSystem: ActorSystem) extends GrouppreService {

  case object NoSeqStateDate extends RuntimeException("No SeqStateDate in response from group found")

  case object NoGroupPre extends RuntimeException("No GroupPre in response from group found")

  override implicit val ec: ExecutionContext = actorSystem.dispatcher

  private val groupPreExt = GroupPreExtension(actorSystem)

  /** Carrega os grupos pre definidos */
  override protected def doHandleLoadGroupsPre(idGrupoPai: Option[Int], clientData: ClientData):
    Future[HandlerResult[ResponseLoadGroupsPre]] =
    authorized(clientData) { implicit client ⇒
      for {
        gruposPre <- groupPreExt.loadGroupsPre(client.userId, idGrupoPai)
        gruposApi = gruposPre map(gp =>  ApiGroupPre(groupId = gp.groupId,
          hasChildrem = gp.possuiFilhos,
          acessHash = gp.acessHash,
          order = gp.ordem,
          parentId = if (gp.idPai > 0) Some(gp.idPai) else None ))
      } yield (Ok(ResponseLoadGroupsPre(groups = gruposApi.toIndexedSeq)))
    }

  /** Change group parent */
  override protected def doHandleChangeGroupParent(groupId: Int, parentId: Int, clientData: ClientData) :
  Future[HandlerResult[ResponseSeq]] = {
    FastFuture.successful(Error(CommonRpcErrors.NotSupportedInOss))
  }

  /** Create a new groupPre */
  override protected def doHandleChangeGroupPre(groupId: Int, isGroupPre: Boolean, idParent: Option[Int], clientData: ClientData):
  Future[HandlerResult[ResponseChangeGroupPre]] =
      authorized(clientData) { implicit client ⇒
        for{
          ack <- groupPreExt.create(groupId, idParent.getOrElse(-1)) if(isGroupPre)
          ack <- groupPreExt.remove(groupId) if(!isGroupPre)
          setState = ack.seqState.getOrElse(throw NoSeqStateDate)
          group = ack.group.getOrElse(throw NoGroupPre)
          apiGroup = ApiGroupPre(group.groupId, group.possuiFilhos, group.acessHash, group.ordem, if (group.idPai > 0) Some(group.idPai) else None)
        }yield(Ok(ResponseChangeGroupPre(setState.seq, setState.state.toByteArray, Instant.now().toEpochMilli, apiGroup)))
    }
}
