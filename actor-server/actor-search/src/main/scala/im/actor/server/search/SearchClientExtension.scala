package im.actor.server.search

import akka.actor._
import com.sksamuel.elastic4s.{ ElasticClient, ElasticsearchClientUri }
import org.elasticsearch.common.settings.Settings

sealed trait SearchClientExtension extends Extension

final class SearchClientExtensionImpl(system: ActorSystem) extends SearchClientExtension {
  private val elasticConfig: ElasticSearchConfig = ElasticSearchConfig.load.getOrElse(sys.error("Failed to load elastic search config"))
  val indexerConfig: IndexerConfig = IndexerConfig.load.getOrElse(sys.error("Failed to load indexer config"))
  val searchQueryConfig: SearchQueryConfig = SearchQueryConfig.load.getOrElse(sys.error("Failed to load search query config"))

  val client: ElasticClient = {
    val settings = Settings.builder().put("cluster.name", elasticConfig.clusterName).build()
    val elasticUri = ElasticsearchClientUri(elasticConfig.host, elasticConfig.port)
    ElasticClient.transport(settings, elasticUri)
  }
}

object SearchClientExtension extends ExtensionId[SearchClientExtensionImpl] with ExtensionIdProvider {
  override def lookup = SearchClientExtension

  override def createExtension(system: ExtendedActorSystem) = new SearchClientExtensionImpl(system)
}
