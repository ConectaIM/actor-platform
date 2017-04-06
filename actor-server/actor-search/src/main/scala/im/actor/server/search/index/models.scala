package im.actor.server.search.index

import com.sksamuel.elastic4s.source.Indexable
import com.sksamuel.elastic4s.{ HitAs, RichSearchHit }
import im.actor.server.model.Peer
import play.api.libs.json.Json

sealed trait ContentType

object ContentType {
  case object Text extends ContentType
  case object Link extends ContentType
  case object Document extends ContentType
  case object Photo extends ContentType

  def toInt: PartialFunction[ContentType, Int] = {
    case Text     ⇒ 1
    case Link     ⇒ 2
    case Document ⇒ 3
    case Photo    ⇒ 4
  }

  def fromInt: PartialFunction[Int, ContentType] = {
    case 1 ⇒ Text
    case 2 ⇒ Link
    case 3 ⇒ Document
    case 4 ⇒ Photo
  }

}

object IndexedMessage {
  import io.circe._
  import io.circe.syntax._
  import io.circe.parser
  import io.circe.generic.auto._

  implicit val contentTypeEncoder: Encoder[ContentType] = Encoder.instance { ct ⇒ ContentType.toInt(ct).asJson }
  implicit val contentTypeDecoder: Decoder[ContentType] = Decoder.instance { cursor ⇒ cursor.as[Int].map(ContentType.fromInt) }

  implicit val messageHitAs: HitAs[IndexedMessage] = new HitAs[IndexedMessage] {
    override def as(hit: RichSearchHit): IndexedMessage = {
      parser.decode[IndexedMessage](hit.sourceAsString).fold(
        err ⇒ sys.error("Failed to parse response from elasticsearch, cause: " + err),
        mess ⇒ mess
      )
    }
  }

  implicit val messageIndexable = new Indexable[IndexedMessage] {
    override def json(m: IndexedMessage): String = m.asJson.noSpaces
  }
}

final case class IndexedMessage(
  randomId:    Long,
  ts:          Long,
  senderId:    Int,
  peer:        Peer,
  contentType: ContentType,
  content:     String,
  isPublic:    Boolean,
  users:       Set[Int]
)
