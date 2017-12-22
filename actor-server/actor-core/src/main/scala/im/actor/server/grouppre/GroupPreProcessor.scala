package im.actor.server.grouppre

import java.time.Instant

import akka.actor.{ActorSystem, Props}
import im.actor.serialization.ActorSerializer
import im.actor.server.GroupPreCommands.{ChangeParent, Create, Remove}
import im.actor.server.GroupPreQueries.GetGroupsPre
import im.actor.server.{GroupPreCommands, GroupPreQueries}
import im.actor.server.cqrs.{Processor, TaggedEvent}
import im.actor.server.db.DbExtension
import im.actor.server.dialog.DialogExtension
import im.actor.server.group.GroupExtension
import im.actor.server.sequence.SeqUpdatesExtension
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

trait GroupPreEvent extends TaggedEvent {
  val ts: Instant
  override def tags: Set[String] = Set("grouppre")
}

trait GroupPreCommand {
  val groupId: Int
}

trait GroupPreQuery {
  val userId: Int
}

case object StopProcessor

object GroupPreProcessor {
  def register(): Unit =
    ActorSerializer.register(
      300001 → classOf[GroupPreCommands.Create],
      300002 → classOf[GroupPreCommands.CreateAck],
      //300003 → classOf[GroupPreQueries.GetApiStruct],
      //300004 → classOf[GroupPreQueries.GetApiStructResponse],
      300005 → classOf[GroupPreQueries.GetGroupsPre],
      300006 → classOf[GroupPreQueries.GetGroupsPreResponse]//,
//      300007 → classOf[GroupPreEvents.Created]
    )

  def persistenceIdFor(groupPreId: Int): String = s"Grouppre-${groupPreId}"
  private[grouppre] def props: Props = Props(classOf[GroupPreProcessor])
}

/**
  * Created by 98379720172 on 31/01/17.
  */
private[grouppre] final class GroupPreProcessor
  extends Processor[GroupPreState]
  with GroupPreCommandHandlers
  with GroupPreQueryHandlers {

  protected implicit val ec: ExecutionContext = context.dispatcher
  protected implicit val system: ActorSystem = context.system

  protected val userId = self.path.name.toInt

  protected val db: Database = DbExtension(system).db
  protected val groupPreExt = GroupPreExtension(system)
  protected val seqUpdExt = SeqUpdatesExtension(system)
  protected val groupExt = GroupExtension(system)
  protected val dialogExt = DialogExtension(system)

  override protected def handleCommand: Receive = {
    case c: Create => create(c)
    case r: Remove => remove(r)
    case cp: ChangeParent => changeParent(cp)
  }

  override protected def handleQuery: PartialFunction[Any, Future[Any]] = {
    case ggp:GetGroupsPre => loadGroupsPre(ggp.groupFatherId)
  }

  override protected def getInitialState: GroupPreState = GroupPreState.empty

  override def persistenceId: String = GroupPreProcessor.persistenceIdFor(userId)

}
