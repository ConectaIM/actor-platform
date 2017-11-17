package im.actor.server.grouppre

import akka.pattern.ask
import akka.util.Timeout
import im.actor.server.GroupPre
import im.actor.server.GroupPreCommands.{Create, CreateAck}
import im.actor.server.GroupPreQueries.{GetGroupsPre, GetGroupsPreResponse}
import im.actor.server.dialog.UserAcl

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by 98379720172 on 08/02/17.
  */

trait GroupPreOperations extends Commands with Queries

private[grouppre] sealed trait Commands extends UserAcl{

  val processorRegion: GroupPreProcessorRegion

  implicit val timeout:Timeout
  implicit val ec: ExecutionContext

  def create(groupId: Int, groupFatherId: Int) : Future[CreateAck] =
    (processorRegion.ref ? Create(groupId=groupId, groupFatherId=groupFatherId)).mapTo[CreateAck]
}

private[grouppre] sealed trait Queries{

  val viewRegion: GroupPreViewRegion

  implicit val timeout:Timeout
  implicit val ec: ExecutionContext

  def loadGroupsPre(clientUserId: Int, idGrupoPai:Option[Int]): Future[Seq[GroupPre]] =
    if(idGrupoPai.isDefined){
      (viewRegion.ref ? GetGroupsPre(groupFatherId = idGrupoPai.get)).mapTo[GetGroupsPreResponse].map(_.groups)
    }else{
      (viewRegion.ref ? GetGroupsPre()).mapTo[GetGroupsPreResponse].map(_.groups)
    }
}