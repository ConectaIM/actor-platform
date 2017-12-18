package im.actor.server.grouppre

import akka.pattern.ask
import akka.util.Timeout
import im.actor.server.GroupPre
import im.actor.server.GroupPreCommands.{ChangeParent, ChangeParentAck, Create, CreateAck, Remove, RemoveAck}
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

  def create(groupId: Int) : Future[CreateAck] =
    (processorRegion.ref ? Create(groupId=groupId)).mapTo[CreateAck]

  def remove(groupId: Int) : Future[RemoveAck] =
    (processorRegion.ref ? Remove(groupId=groupId)).mapTo[RemoveAck]

  def changeParent(groupId: Int, parentId: Int) : Future[ChangeParentAck] =
    (processorRegion.ref ? ChangeParent(groupId=groupId, parentId=parentId)).mapTo[ChangeParentAck]
}

private[grouppre] sealed trait Queries{

  val viewRegion: GroupPreViewRegion

  implicit val timeout:Timeout
  implicit val ec: ExecutionContext

  def loadGroupsPre(clientUserId: Int, idGrupoPai:Option[Int]): Future[Seq[GroupPre]] =
      (viewRegion.ref ? GetGroupsPre(groupFatherId = idGrupoPai.getOrElse(0))).mapTo[GetGroupsPreResponse].map(_.groups)

}