package im.actor.server.grouppre

import akka.actor.{ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import akka.event.Logging
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Created by 98379720172 on 08/02/17.
  */
sealed trait GroupPreExtension extends Extension

final class GroupPreExtensionImpl(val actorSystem: ActorSystem) extends GroupPreExtension with GroupPreOperations {

  GroupPreProcessor.register()

  implicit val system = actorSystem

  import system.dispatcher
  val log = Logging(system, getClass)

  lazy val processorRegion: GroupPreProcessorRegion = GroupPreProcessorRegion.start()(system)
  lazy val viewRegion: GroupPreViewRegion = GroupPreViewRegion(processorRegion.ref)


  implicit val timeout: Timeout = Timeout(20.seconds)
  implicit val ec: ExecutionContext = system.dispatcher

}

object GroupPreExtension extends ExtensionId[GroupPreExtensionImpl] with ExtensionIdProvider {
  override def lookup = GroupPreExtension
  override def createExtension(system: ExtendedActorSystem) = new GroupPreExtensionImpl(system)
}
