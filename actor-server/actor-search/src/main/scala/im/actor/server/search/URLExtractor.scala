package im.actor.server.search

import java.util

import org.nibor.autolink.LinkType.{ EMAIL, URL, WWW }
import org.nibor.autolink._

import scala.collection.JavaConverters._

object URLExtractor {

  private val linkExtractor = LinkExtractor.builder().linkTypes(util.EnumSet.of(EMAIL, URL, WWW)).build()

  final def extractLinks(input: String): Seq[String] = {
    linkExtractor.extractLinks(input).asScala.map { link ⇒
      input.substring(link.getBeginIndex, link.getEndIndex)
    }.toIndexedSeq
  }

}
