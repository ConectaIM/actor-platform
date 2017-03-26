package im.actor.server.search.index

import im.actor.server.model.Peer

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
