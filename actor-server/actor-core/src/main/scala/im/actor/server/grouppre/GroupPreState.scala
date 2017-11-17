package im.actor.server.grouppre

import java.time.Instant

import akka.persistence.SnapshotMetadata
import im.actor.api.rpc.grouppre.ApiGroupPre
import im.actor.server.cqrs.{Event, ProcessorState}

/**
  * Created by 98379720172 on 31/01/17.
  */

private[grouppre] object GroupPreState {
  def empty: GroupPreState =
    GroupPreState(
      groupId = 0,
      groupFatherId = 0,
      childrens = IndexedSeq(),
      createdAt = None,
      creatorUserId = 0,
      deletedAt = None
    )
}

private[grouppre] final case class GroupPreState(
  groupId:            Int,
  groupFatherId:      Int,
  childrens:          IndexedSeq[ApiGroupPre],
  createdAt:          Option[Instant],
  creatorUserId:      Int,
  deletedAt:          Option[Instant]
) extends ProcessorState[GroupPreState]{

  val isNotCreated = createdAt.isEmpty

  val isCreated = createdAt.nonEmpty

  val isDeleted = deletedAt.nonEmpty

  val hasChildrens = !childrens.isEmpty

  override def updated(e: Event): GroupPreState = this
  override def withSnapshot(metadata: SnapshotMetadata, snapshot: Any): GroupPreState = this
}
