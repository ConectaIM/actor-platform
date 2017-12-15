package im.actor.server.persist

import im.actor.server.db.ActorPostgresDriver.api._
import im.actor.server.model.MessageType

object MessageTypeColumnType {
  implicit val messageTypeCT = MappedColumnType.base[MessageType, Int](_.intValue, MessageType.apply)
}
