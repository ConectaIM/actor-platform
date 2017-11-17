package im.actor.server.grouppre

import java.time.Instant

import im.actor.api.rpc.grouppre.{ApiGroupPre, UpdateGroupPreCreated}
import im.actor.api.rpc.groups.ApiGroupType
import im.actor.server.GroupPre
import im.actor.server.GroupPreCommands.{Create, CreateAck}
import im.actor.server.persist.UserRepo
import im.actor.server.persist.grouppre.{PublicGroup, PublicGroupRepo}


/**
  * Created by 98379720172 on 03/02/17.
  */
private [grouppre] trait GroupPreCommandHandlers {

  this: GroupPreProcessor =>

  protected def create(cmd: Create): Unit = {
    val createdAt = Instant.now
    for {
      apiGroup <- groupExt.getApiStruct(cmd.groupId, cmd.userId)
      grupoPublico = PublicGroup(id = apiGroup.id,
        typ = (apiGroup.groupType match {
          case Some(ApiGroupType.GROUP) => "G"
          case Some(ApiGroupType.CHANNEL) => "C"
          case _ => "G"
        }),
        order = 0,
        hasChildrem = false,
        parentId = if(cmd.groupFatherId > 0) Some(cmd.groupFatherId) else None,
        apiGroup.accessHash
      )

      _ <- db.run(
        (for {
          _ ← PublicGroupRepo.createOrUpdate(grupoPublico)
          _ ← PublicGroupRepo.atualizaPossuiFilhos(cmd.groupFatherId, true)
        } yield ())
      )

      update = UpdateGroupPreCreated(ApiGroupPre(
        groupId = apiGroup.id,
        hasChildrem = false,
        acessHash = apiGroup.accessHash,
        order = 0
      ))

      activeUsersIds <- db.run(UserRepo.activeUsersIds)
      seqState <- seqUpdExt.broadcastClientUpdate(cmd.userId, cmd.authId, activeUsersIds.toSet, update)
      
    }yield(CreateAck(Some(seqState),
        Some(GroupPre(grupoPublico.id,
          grupoPublico.typ,
          grupoPublico.order,
          grupoPublico.hasChildrem,
          grupoPublico.parentId.getOrElse(0),
          grupoPublico.accessHash))))
  }
}