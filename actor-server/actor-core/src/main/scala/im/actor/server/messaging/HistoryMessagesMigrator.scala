package im.actor.server.messaging

import akka.actor.ActorSystem
import cats.data.Xor
import im.actor.api.rpc.messaging._
import im.actor.server.db.DbExtension
import im.actor.server.dialog.HistoryUtils
import im.actor.server.migrations.Migration
import im.actor.server.model.{HistoryMessage, MessageType}
import im.actor.server.persist.HistoryMessageRepo

import scala.concurrent.duration._
import scala.concurrent.Future

object HistoryMessagesMigrator extends Migration{
  override protected def migrationName: String = "2017-11-02-MigrateMessageHistoryType"

  override protected def migrationTimeout: Duration = 24.hours

  override protected def startMigration()(implicit system: ActorSystem): Future[Unit] = {

    import scala.concurrent.ExecutionContext.Implicits.global

    for {
        hists <- DbExtension(system).db.run(HistoryMessageRepo.nullMessageType)
        hists2 = hists map(hist => {
          Xor.fromEither(ApiMessage.parseFrom(hist.messageContentData)) map { messageContent =>
            (hist, HistoryUtils.getMessageType(messageContent))
          }
        })
      } yield {
      hists2 map{
        case Xor.Right((histMessage:HistoryMessage, messageType:Some[MessageType])) => {
          DbExtension(system).db.run(HistoryMessageRepo.updateMessageType(histMessage.userId, histMessage.randomId, histMessage.peer.`type`, histMessage.peer.id, messageType))
        }
        case _ => ()
      }
      ()
    }
  }
}
