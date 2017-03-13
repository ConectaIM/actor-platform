package im.actor.server

import im.actor.serialization.ActorSerializer
import im.actor.server.sequence.{ SeqState, SeqStateDate }

object CommonSerialization {
  def register(): Unit = {
    ActorSerializer.register(
      100 → classOf[im.actor.server.event.TSEvent],
      101 → classOf[SeqState],
      102 → classOf[SeqStateDate]
    )
  }
}
