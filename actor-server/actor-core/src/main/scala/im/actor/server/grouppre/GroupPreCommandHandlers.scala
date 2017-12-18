package im.actor.server.grouppre

import im.actor.api.rpc.grouppre.{ApiGroupPre, UpdateGroupPreCreated, UpdateGroupPreParentChanged, UpdateGroupPreRemoved}
import im.actor.api.rpc.groups.ApiGroupType
import im.actor.server.GroupPre
import akka.pattern.pipe
import im.actor.server.GroupPreCommands.{ChangeParent, ChangeParentAck, Create, CreateAck, Remove, RemoveAck}
import im.actor.server.persist.UserRepo
import im.actor.server.persist.grouppre.{PublicGroup, PublicGroupRepo}
import im.actor.server.sequence.SeqState
import org.joda.time.Instant

import scala.concurrent.Future

/**
  * Created by 98379720172 on 03/02/17.
  */
private [grouppre] trait GroupPreCommandHandlers {

  this: GroupPreProcessor =>

  protected def create(cmd: Create): Unit = {
    val createdAt = Instant.now
    val result: Future[CreateAck] = for {
      apiGroup <- groupExt.getApiStruct(cmd.groupId, cmd.userId)
      publicGroup = PublicGroup(id = apiGroup.id,
        typ = (apiGroup.groupType match {
          case Some(ApiGroupType.GROUP) => "G"
          case Some(ApiGroupType.CHANNEL) => "C"
          case _ => "G"
        }),
        accessHash = apiGroup.accessHash
      )

      _ <- db.run(
        (for {
          _ ← PublicGroupRepo.createOrUpdate(publicGroup)
        } yield ())
      )

      update = UpdateGroupPreCreated(ApiGroupPre(
        groupId = apiGroup.id,
        hasChildrem = false,
        acessHash = apiGroup.accessHash,
        order = 0,
        parentId = Option(publicGroup.parentId)
      ))

      activeUsersIds <- db.run(UserRepo.activeUsersIds)
      seqState <- seqUpdExt.broadcastClientUpdate(cmd.userId, cmd.authId, activeUsersIds.toSet, update)
      
    }yield(CreateAck(Some(seqState)))

    result pipeTo sender() onFailure {
      case e ⇒
        log.error(e, "Failed to create group pre")
    }
  }

  protected def remove(cmd: Remove): Unit = {
    val result: Future[RemoveAck] = for {
      apiGroup <- groupExt.getApiStruct(cmd.groupId, cmd.userId)
      publicGroup <- db.run(PublicGroupRepo.findById(cmd.groupId))

      seqState:SeqState <- publicGroup match {
        case Some(pg) => {
          for{
            _ <- db.run(for {
              _ <- PublicGroupRepo.delete(pg.id)
              hasChildren <- PublicGroupRepo.possuiFilhos(pg.parentId)
              _ <- PublicGroupRepo.atualizaPossuiFilhos(pg.parentId, hasChildren)
            } yield ())

            update = UpdateGroupPreRemoved(ApiGroupPre(
              groupId = apiGroup.id,
              hasChildrem = false,
              acessHash = apiGroup.accessHash,
              order = 0,
              parentId = Option(pg.parentId)
            ))

            activeUsersIds <- db.run(UserRepo.activeUsersIds)
            seqState <- seqUpdExt.broadcastClientUpdate(cmd.userId, cmd.authId, activeUsersIds.toSet, update)
          } yield seqState
        }
        case None =>{

          val update = UpdateGroupPreRemoved(ApiGroupPre(
            groupId = apiGroup.id,
            hasChildrem = false,
            acessHash = apiGroup.accessHash,
            order = 0,
            parentId = None
          ))

          for{
            activeUsersIds <- db.run(UserRepo.activeUsersIds)
            seqState <- seqUpdExt.broadcastClientUpdate(cmd.userId, cmd.authId, activeUsersIds.toSet, update)
          } yield seqState
        }
      }
    } yield (RemoveAck(Option(seqState)))

    result pipeTo sender() onFailure {
      case e ⇒
        log.error(e, "Failed to remove Group Pre")
    }
  }

  protected def changeParent(cmd: ChangeParent): Unit = {

    val result: Future[ChangeParentAck] = for{

      previous <- db.run(for{
        retorno <- PublicGroupRepo.findById(cmd.groupId)
        _ <- PublicGroupRepo.updateParent(cmd.groupId, cmd.parentId)
        _ ← PublicGroupRepo.atualizaPossuiFilhos(cmd.parentId, true)
      } yield(retorno))

      update = UpdateGroupPreParentChanged(cmd.groupId, cmd.parentId, previous.get.parentId)

      activeUsersIds <- db.run(UserRepo.activeUsersIds)
      seqState <- seqUpdExt.broadcastClientUpdate(cmd.userId, cmd.authId, activeUsersIds.toSet, update)
    } yield (ChangeParentAck(Some(seqState)))
  }


}