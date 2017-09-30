package im.actor.server.model

final case class MessageType(intValue: Int)
object MessageType {
  val Photo = MessageType(1)
  val Video = MessageType(2)
  val Document = MessageType(3)
}
