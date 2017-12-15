package im.actor.server.search

import akka.actor.ActorSystem
import akka.http.scaladsl.util.FastFuture
import com.optimaize.langdetect.i18n.LdLocale
import im.actor.api.rpc.search.{ ApiSearchAndCondition, ApiSearchCondition, ApiSearchOrCondition, ApiSearchPieceText }
import im.actor.server.user.UserExtension

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

trait LanguageFieldDetection {

  protected def system: ActorSystem
  protected implicit def ec: ExecutionContext

  /**
   * Detect languages by text in search condition(if text is present). Convert these languages into names of index fields.
   * If we are 90% confident about language detection, and we index text with analyzer for this language - use only this language for search.
   * Otherwise get information about user's preferred languages; add weight of preferred languages to other detected languages,
   * and filter out languages with confidence lower than 50%.
   *
   * @param cond search condition
   * @param userId client user id
   * @return List of language fields, as they present in elasicsearch messages index
   */
  protected def detectContentFields(cond: ApiSearchCondition, userId: Int): Future[Vector[String]] =
    extractText(cond) map { text ⇒
      val detectedLangs = LangDetectorExtension(system).probabilities(text)
      val primaryCandidate = detectedLangs find { case (loc, prob) ⇒ prob >= 0.9 } flatMap { case (loc, _) ⇒ toIndexField(loc.getLanguage) }
      primaryCandidate match {
        case Some(field) ⇒ FastFuture.successful(Vector(field))
        case None ⇒
          UserExtension(system).getApiStruct(userId, 0, 0L) map { user ⇒
            val allLangs = (user.preferredLanguages flatMap { lang ⇒ Try(LdLocale.fromString(lang) → 0.5).toOption }).foldLeft(detectedLangs) {
              case (acc, el) ⇒
                val (loc, prob) = el
                if (acc.contains(loc)) acc.updated(loc, acc(loc) + prob) else acc + el
            }
            (allLangs collect {
              case (loc, prob) if prob >= 0.5 ⇒ toIndexField(loc.getLanguage)
            }).flatten.toVector
          }
      }
    } getOrElse FastFuture.successful(Vector.empty)

  private def extractText(cond: ApiSearchCondition): Option[String] = cond match {
    case ApiSearchPieceText(text)        ⇒ Some(text)
    case ApiSearchOrCondition(subconds)  ⇒ (subconds flatMap extractText).headOption
    case ApiSearchAndCondition(subconds) ⇒ (subconds flatMap extractText).headOption
    case _                               ⇒ None
  }

  private def toIndexField(lang: String): Option[String] = lang.toLowerCase match {
    case "ar"                  ⇒ Some("arabic")
    case "en"                  ⇒ Some("english")
    case "fr"                  ⇒ Some("french")
    case "de"                  ⇒ Some("german")
    case "rp"                  ⇒ Some("portuguese")
    case "ru"                  ⇒ Some("russian")
    case "es"                  ⇒ Some("spanish")
    case l if l.contains("zh") ⇒ Some("chinese")
    case _                     ⇒ None
  }

}
