package im.actor.server.persist

import com.github.tototoshi.slick.PostgresJodaSupport._
import im.actor.server.model.{HistoryMessage, MessageType, Peer, PeerType}
import org.joda.time.DateTime
import slick.dbio.Effect.{Read, Write}
import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._
import slick.jdbc.GetResult
import slick.profile.{FixedSqlAction, FixedSqlStreamingAction, SqlAction, SqlStreamingAction}
import MessageTypeColumnType._

final class HistoryMessageTable(tag: Tag) extends Table[HistoryMessage](tag, "history_messages") {

  def userId = column[Int]("user_id", O.PrimaryKey)

  def peerType = column[Int]("peer_type", O.PrimaryKey)

  def peerId = column[Int]("peer_id", O.PrimaryKey)

  def date = column[DateTime]("date", O.PrimaryKey)

  def senderUserId = column[Int]("sender_user_id", O.PrimaryKey)

  def randomId = column[Long]("random_id", O.PrimaryKey)

  def messageContentHeader = column[Int]("message_content_header")

  def messageContentData = column[Array[Byte]]("message_content_data")

  def deletedAt = column[Option[DateTime]]("deleted_at")

  def messageType = column[Option[MessageType]]("message_type")

  def * = (userId, peerType, peerId, date, senderUserId, randomId, messageContentHeader, messageContentData, deletedAt, messageType) <>
    (applyHistoryMessage.tupled, unapplyHistoryMessage)

  private def applyHistoryMessage: (Int, Int, Int, DateTime, Int, Long, Int, Array[Byte], Option[DateTime], Option[MessageType]) ⇒ HistoryMessage = {
    case (userId, peerType, peerId, date, senderUserId, randomId, messageContentHeader, messageContentData, deletedAt, messageType) ⇒
      HistoryMessage(
        userId = userId,
        peer = Peer(PeerType.fromValue(peerType), peerId),
        date = date,
        senderUserId = senderUserId,
        randomId = randomId,
        messageContentHeader = messageContentHeader,
        messageContentData = messageContentData,
        deletedAt = deletedAt,
        messageType = messageType
      )
  }

  private def unapplyHistoryMessage: HistoryMessage ⇒ Option[(Int, Int, Int, DateTime, Int, Long, Int, Array[Byte], Option[DateTime], Option[MessageType])] = { historyMessage ⇒
    HistoryMessage.unapply(historyMessage) map {
      case (userId, peer, date, senderUserId, randomId, messageContentHeader, messageContentData, deletedAt, messageType) ⇒
        (userId, peer.typ.value, peer.id, date, senderUserId, randomId, messageContentHeader, messageContentData, deletedAt, messageType)
    }
  }
}

object HistoryMessageRepo {
  private val SharedUserId = 0

  val messages = TableQuery[HistoryMessageTable]
  val messagesC = Compiled(messages)

  val notDeletedMessages = messages.filter(_.deletedAt.isEmpty)

  val nullMessageType = messages.filter(_.messageType.isEmpty).result

  val documentsMessageType = messages.filter(_.messageType === MessageType.Document).result

  val withoutServiceMessages = notDeletedMessages.filter(_.messageContentHeader =!= 2)

  def byUserIdPeer(userId: Rep[Int], peerType: Rep[Int], peerId: Rep[Int]) =
    notDeletedMessages
      .filter(m ⇒ m.userId === userId && m.peerType === peerType && m.peerId === peerId)

  def create(message: HistoryMessage): FixedSqlAction[Int, NoStream, Write] =
    messagesC += message

  def create(newMessages: Seq[HistoryMessage]): FixedSqlAction[Option[Int], NoStream, Write] =
    messagesC ++= newMessages

  def find(userId: Int, peer: Peer, dateOpt: Option[DateTime], limit: Int): FixedSqlStreamingAction[Seq[HistoryMessage], HistoryMessage, Read] = {
    val baseQuery = notDeletedMessages
      .filter(m ⇒
        m.userId === userId &&
          m.peerType === peer.typ.value &&
          m.peerId === peer.id)

    val query = dateOpt match {
      case Some(date) ⇒
        baseQuery.filter(_.date <= date).sortBy(_.date.desc)
      case None ⇒
        baseQuery.sortBy(_.date.asc)
    }
    query.take(limit).result
  }

  def find(userId: Int, peer: Peer, dateOpt: Option[DateTime], limit: Int, messageType:MessageType): FixedSqlStreamingAction[Seq[HistoryMessage], HistoryMessage, Read] = {
    val baseQuery = notDeletedMessages
      .filter(m ⇒
        m.userId === userId &&
          m.peerType === peer.typ.value &&
          m.peerId === peer.id &&
          m.messageType === messageType)

    val query = dateOpt match {
      case Some(date) ⇒
        baseQuery.filter(_.date <= date).sortBy(_.date.desc)
      case None ⇒
        baseQuery.sortBy(_.date.asc)
    }
    query.take(limit).result
  }

  private val afterC = Compiled { (userId: Rep[Int], peerType: Rep[Int], peerId: Rep[Int], date: Rep[DateTime], limit: ConstColumn[Long]) ⇒
    byUserIdPeer(userId, peerType, peerId)
      .filter(_.date >= date)
      .sortBy(_.date.asc)
      .take(limit)
  }

  def findAfter(userId: Int, peer: Peer, date: DateTime, limit: Long) =
    afterC((userId, peer.typ.value, peer.id, date, limit)).result

  private val metaAfterC = Compiled { (userId: Rep[Int], peerType: Rep[Int], peerId: Rep[Int], date: Rep[DateTime], limit: ConstColumn[Long]) ⇒
    byUserIdPeer(userId, peerType, peerId)
      .filter(_.date > date)
      .sortBy(_.date.asc)
      .take(limit)
      .map(hm ⇒ (hm.randomId, hm.date, hm.senderUserId, hm.messageContentHeader))
  }

  def findMetaAfter(userId: Int, peer: Peer, date: DateTime, limit: Long) =
    metaAfterC((userId, peer.typ.value, peer.id, date, limit)).result

  private val beforeC = Compiled { (userId: Rep[Int], peerId: Rep[Int], peerType: Rep[Int], date: Rep[DateTime], limit: ConstColumn[Long]) ⇒
    byUserIdPeer(userId, peerType, peerId)
      .filter(_.date <= date)
      .sortBy(_.date.asc)
      .take(limit)
  }

  private val beforeExclC = Compiled { (userId: Rep[Int], peerId: Rep[Int], peerType: Rep[Int], date: Rep[DateTime], limit: ConstColumn[Long]) ⇒
    byUserIdPeer(userId, peerType, peerId)
      .filter(_.date < date)
      .sortBy(_.date.asc)
      .take(limit)
  }

  private val byUserIdPeerRidC = Compiled { (userId: Rep[Int], peerType: Rep[Int], peerId: Rep[Int], randomId: Rep[Long]) ⇒
    byUserIdPeer(userId, peerType, peerId).filter(_.randomId === randomId)
  }

  def existstWithRandomId(userId: Int, peer: Peer, randomId: Long): DBIO[Boolean] =
    byUserIdPeerRidC.applied((userId, peer.typ.value, peer.id, randomId)).exists.result

  def findBefore(userId: Int, peer: Peer, date: DateTime, limit: Long) =
    beforeC((userId, peer.typ.value, peer.id, date, limit)).result

  def findBidi(userId: Int, peer: Peer, date: DateTime, limit: Long) =
    (beforeExclC.applied((userId, peer.typ.value, peer.id, date, limit)) ++
      afterC.applied((userId, peer.typ.value, peer.id, date, limit))).result

  def findBySender(senderUserId: Int, peer: Peer, randomId: Long): FixedSqlStreamingAction[Seq[HistoryMessage], HistoryMessage, Read] =
    notDeletedMessages.filter(m ⇒ m.senderUserId === senderUserId && m.peerType === peer.typ.value && m.peerId === peer.id && m.randomId === randomId).result

  def findUserIds(peer: Peer, randomIds: Set[Long]): DBIO[Seq[Int]] =
    notDeletedMessages
      .filter(m ⇒ m.peerType === peer.typ.value && m.peerId === peer.id && (m.randomId inSet randomIds))
      .map(_.userId)
      .result

  def findNewest(userId: Int, peer: Peer): SqlAction[Option[HistoryMessage], NoStream, Read] =
    notDeletedMessages
      .filter(m ⇒ m.userId === userId && m.peerType === peer.typ.value && m.peerId === peer.id)
      .sortBy(_.date.desc)
      .take(1)
      .result
      .headOption

  def find(userId: Int, peer: Peer): FixedSqlStreamingAction[Seq[HistoryMessage], HistoryMessage, Read] =
    notDeletedMessages
      .filter(m ⇒ m.userId === userId && m.peerType === peer.typ.value && m.peerId === peer.id)
      .sortBy(_.date.desc)
      .result

  def find(userId: Int, peer: Peer, randomIds: Set[Long]): FixedSqlStreamingAction[Seq[HistoryMessage], HistoryMessage, Read] =
    notDeletedMessages.filter(m ⇒ m.userId === userId && m.peerType === peer.typ.value && m.peerId === peer.id && (m.randomId inSet randomIds)).result

  def updateContentAll(userIds: Set[Int], randomId: Long, peerType: PeerType, peerIds: Set[Int],
                       messageContentHeader: Int, messageContentData: Array[Byte]): FixedSqlAction[Int, NoStream, Write] =
    notDeletedMessages
      .filter(m ⇒ m.randomId === randomId && m.peerType === peerType.value)
      .filter(_.peerId inSet peerIds)
      .filter(_.userId inSet userIds)
      .map(m ⇒ (m.messageContentHeader, m.messageContentData))
      .update((messageContentHeader, messageContentData))

  def updateMessageType(userId: Int, randomId: Long, peerType: PeerType, peerId: Int,
                        messageType: Option[MessageType]): FixedSqlAction[Int, NoStream, Write] =
    messages
      .filter(m ⇒ m.randomId === randomId && m.peerType === peerType.value)
      .filter(_.peerId === peerId)
      .filter(_.userId === userId)
      .map(_.messageType)
      .update(messageType)

  private implicit val getMessageResult: GetResult[HistoryMessage] = GetResult(r ⇒
    HistoryMessage(
      userId = r.nextInt,
      peer = Peer(PeerType.fromValue(r.nextInt), r.nextInt),
      date = getDatetimeResult(r),
      senderUserId = r.nextInt,
      randomId = r.nextLong,
      messageContentHeader = r.nextInt,
      messageContentData = r.nextBytes,
      deletedAt = getDatetimeOptionResult(r),
      messageType = r.nextIntOption() match {
        case i:Some[Int] => Some(MessageType(i.get))
        case _ => None
      }
    ))
  private val ServiceHeader = 2

  def uniqueAsc(fromTs: Long, limit: Int): SqlStreamingAction[Vector[HistoryMessage], HistoryMessage, Effect] = {

    val date = new DateTime(fromTs)
    sql"""select distinct on (date, random_id) user_id, peer_type, peer_id, date, sender_user_id, random_id, message_content_header, message_content_data, deleted_at from history_messages
         where message_content_header != $ServiceHeader
         and date > $date
         and deleted_at is null
         order by date asc, random_id asc
         limit $limit"""
      .as[HistoryMessage]
  }

  def uniqueAsc(fromDate: DateTime, lastRandomId: Long, limit: Int): SqlStreamingAction[Vector[HistoryMessage], HistoryMessage, Effect] = {
    sql"""select distinct on (date, random_id) user_id, peer_type, peer_id, date, sender_user_id, random_id, message_content_header, message_content_data, deleted_at from history_messages
         where message_content_header != $ServiceHeader
         and date >= $fromDate
         and random_id != lastRandomId
         and deleted_at is null
         order by date asc, random_id asc
         limit $limit"""
      .as[HistoryMessage]
  }

  def haveMessagesBetween(userId: Int, peer: Peer, minDate: DateTime, maxDate: DateTime) =
    notDeletedMessages
      .filter(m ⇒ m.userId === userId && m.peerType === peer.typ.value && m.peerId === peer.id)
      .filter(m ⇒ m.date > minDate && m.date < maxDate && m.senderUserId =!= userId)
      .exists
      .result

  def getUnreadCount(historyOwner: Int, clientUserId: Int, peer: Peer, lastReadAt: DateTime, noServiceMessages: Boolean = false): FixedSqlAction[Int, PostgresDriver.api.NoStream, Read] =
    (if (noServiceMessages) withoutServiceMessages else notDeletedMessages)
      .filter(m ⇒ m.userId === historyOwner && m.peerType === peer.typ.value && m.peerId === peer.id)
      .filter(m ⇒ m.date > lastReadAt && m.senderUserId =!= clientUserId)
      .length
      .result

  def deleteAll(userId: Int, peer: Peer): FixedSqlAction[Int, NoStream, Write] = {
    notDeletedMessages
      .filter(m ⇒ m.userId === userId && m.peerType === peer.typ.value && m.peerId === peer.id)
      .map(_.deletedAt)
      .update(Some(new DateTime))
  }

  def delete(userId: Int, peer: Peer, randomIds: Set[Long]) =
    notDeletedMessages
      .filter(m ⇒ m.userId === userId && m.peerType === peer.typ.value && m.peerId === peer.id)
      .filter(_.randomId inSet randomIds)
      .map(_.deletedAt)
      .update(Some(new DateTime))
}
