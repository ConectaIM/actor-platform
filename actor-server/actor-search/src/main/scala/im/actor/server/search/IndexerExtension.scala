package im.actor.server.search

import akka.actor.{ ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import akka.event.Logging
import akka.stream.{ ActorMaterializer, ActorMaterializerSettings, Materializer, Supervision }

sealed trait IndexerExtension extends Extension

final class IndexerExtensionImpl(system: ActorSystem) extends IndexerExtension {

  val log = Logging(system, getClass)

  private implicit val sys = system
  private implicit val mat: Materializer = {
    val decider: Supervision.Decider = { err ⇒
      log.warning("Uncaught error in stream: {}", err)
      Supervision.resume
    }
    ActorMaterializer(ActorMaterializerSettings.create(system).withSupervisionStrategy(decider))
  }

  private val clientExt = SearchClientExtension(system)
  private val client = clientExt.client
  private val indexName = clientExt.indexerConfig.indexName

  IndexerStream(client, indexName).run()
}

object IndexerExtension extends ExtensionId[IndexerExtensionImpl] with ExtensionIdProvider {
  override def lookup = IndexerExtension
  override def createExtension(system: ExtendedActorSystem) = new IndexerExtensionImpl(system)
}
