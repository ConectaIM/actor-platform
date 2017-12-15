package im.actor.server.search

import akka.actor.{ ActorSystem, ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider }
import com.optimaize.langdetect.LanguageDetectorBuilder
import com.optimaize.langdetect.i18n.LdLocale
import com.optimaize.langdetect.ngram.NgramExtractors
import com.optimaize.langdetect.profiles.LanguageProfileReader
import com.optimaize.langdetect.text.CommonTextObjectFactories

import scala.collection.JavaConversions._

sealed trait LangDetectorExtension extends Extension

final class LangDetectorExtensionImpl(system: ActorSystem) extends LangDetectorExtension {

  private val languageProfiles = new LanguageProfileReader().readAllBuiltIn()

  private val languageDetector = LanguageDetectorBuilder
    .create(NgramExtractors.standard())
    .minimalConfidence(0.7)
    .withProfiles(languageProfiles)
    .build()
  private val textObjectFactory = CommonTextObjectFactories.forIndexing() // customize if defaults aren't good enough

  def detect(text: String): Option[String] = {
    val optLang = languageDetector.detect(textObjectFactory.forText(text))
    system.log.debug("For text: {} detected language: {}", text, optLang)
    if (optLang.isPresent) Some(optLang.get().getLanguage) else None
  }

  def probabilities(text: String): Map[LdLocale, Double] = {
    val probs = languageDetector.getProbabilities(textObjectFactory.forText(text))
    val probLangs = probs map { p ⇒ p.getLocale → p.getProbability }
    system.log.debug("For text: {} detected probabilities: {}", text, probLangs)
    probLangs.toMap
  }

}

object LangDetectorExtension extends ExtensionId[LangDetectorExtensionImpl] with ExtensionIdProvider {
  override def lookup = LangDetectorExtension

  override def createExtension(system: ExtendedActorSystem) = new LangDetectorExtensionImpl(system)
}
