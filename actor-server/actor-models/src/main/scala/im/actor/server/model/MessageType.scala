package im.actor.server.model

final case class MessageType(intValue: Int)
object MessageType {
  val Undefined = MessageType(-1)
  val Photo = MessageType(1)
  val Video = MessageType(2)
  val Document = MessageType(3)
  val Animation = MessageType(4)
  val Voice = MessageType(5)
}
