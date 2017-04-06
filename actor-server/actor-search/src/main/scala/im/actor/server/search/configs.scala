package im.actor.server.search

import com.github.kxbmap.configs.syntax._
import com.typesafe.config.Config
import im.actor.config.ActorConfig

import scala.util.Try

object ElasticSearchConfig {
  def load(config: Config): Try[ElasticSearchConfig] =
    for {
      clusterName ← config.get[Try[String]]("cluster-name")
      host ← config.get[Try[String]]("host")
      port ← config.get[Try[Int]]("port")
    } yield ElasticSearchConfig(clusterName, host, port)

  def load: Try[ElasticSearchConfig] =
    load(ActorConfig.load().getConfig("services.search.elasticsearch"))
}

final case class ElasticSearchConfig(clusterName: String, host: String, port: Int)

object SearchQueryConfig {
  def load(config: Config): Try[SearchQueryConfig] =
    for {
      minContentMatch ← config.get[Try[Int]]("min-content-match")
      cutoffFrequency ← config.get[Try[Double]]("cutoff-frequency")
      sloppiness ← config.get[Try[Int]]("sloppiness")
    } yield SearchQueryConfig(minContentMatch, cutoffFrequency, sloppiness)

  def load: Try[SearchQueryConfig] =
    load(ActorConfig.load().getConfig("services.search.search-query"))
}

final case class SearchQueryConfig(minContentMatch: Int, cutoffFrequency: Double, sloppiness: Int)

object IndexerConfig {
  def load(config: Config): Try[IndexerConfig] =
    for {
      indexName ← config.get[Try[String]]("index-name")
    } yield IndexerConfig(indexName)

  def load: Try[IndexerConfig] =
    load(ActorConfig.load().getConfig("services.search.indexer"))
}

final case class IndexerConfig(indexName: String)
