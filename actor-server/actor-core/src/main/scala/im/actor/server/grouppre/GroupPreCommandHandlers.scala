package im.actor.server.grouppre

import im.actor.api.rpc.grouppre.{ApiGroupPre, UpdateGroupPreCreated, UpdateGroupPreParentChanged, UpdateGroupPreRemoved}
import im.actor.api.rpc.groups.ApiGroupType
import im.actor.server.GroupPre
import akka.pattern.pipe
import im.actor.server.GroupPreCommands.{ChangeParent, ChangeParentAck, Create, CreateAck, Remove, RemoveAck}
import im.actor.server.persist.UserRepo
import im.actor.server.persist.grouppre.{PublicGroup, PublicGroupRepo}
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
        order = 0,
        hasChildrem = false,
        parentId = None,
        apiGroup.accessHash
      )

      _ <- db.run(
        (for {
          _ ← PublicGroupRepo.createOrUpdate(publicGroup)
          //_ ← PublicGroupRepo.atualizaPossuiFilhos(cmd.parentId, true)
        } yield ())
      )

      update = UpdateGroupPreCreated(ApiGroupPre(
        groupId = apiGroup.id,
        hasChildrem = false,
        acessHash = apiGroup.accessHash,
        order = 0,
        parentId = publicGroup.parentId
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

      (seqState, pubGroup) <- publicGroup match {
        case Some(pg) => {
          for{
            _ <- db.run(PublicGroupRepo.delete(pg.id))
            _ <- db.run(for {
              hasChildren <- PublicGroupRepo.possuiFilhos(pg.parentId.get)
              _ <- PublicGroupRepo.atualizaPossuiFilhos(pg.parentId.get, hasChildren)
            } yield ())

            update = UpdateGroupPreRemoved(ApiGroupPre(
              groupId = apiGroup.id,
              hasChildrem = false,
              acessHash = apiGroup.accessHash,
              order = 0,
              parentId = pg.parentId
            ))

            activeUsersIds <- db.run(UserRepo.activeUsersIds)
            seqState <- seqUpdExt.broadcastClientUpdate(cmd.userId, cmd.authId, activeUsersIds.toSet, update)
          } yield (seqState, pg)
        }
        case None =>{
          throw new RuntimeException("Group pre, alread removed")
        }
      }
    } yield (RemoveAck(Some(seqState)))

    result pipeTo sender() onFailure {
      case e ⇒
        log.error(e, "Failed to create group pre")
    }
  }

  protected def changeParent(cmd: ChangeParent): Unit = {

    val result: Future[ChangeParentAck] = for{

      previous <- db.run(for{
        retorno <- PublicGroupRepo.findById(cmd.groupId)
        _ <- PublicGroupRepo.updateParent(cmd.groupId, cmd.parentId)
        _ ← PublicGroupRepo.atualizaPossuiFilhos(cmd.parentId, true)
      } yield(retorno))

      update = UpdateGroupPreParentChanged(cmd.groupId, cmd.parentId, previous.get.parentId.getOrElse(-1))

      activeUsersIds <- db.run(UserRepo.activeUsersIds)
      seqState <- seqUpdExt.broadcastClientUpdate(cmd.userId, cmd.authId, activeUsersIds.toSet, update)
    } yield (ChangeParentAck(Some(seqState)))
  }


}