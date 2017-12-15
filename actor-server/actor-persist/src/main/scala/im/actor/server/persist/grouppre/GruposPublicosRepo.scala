package im.actor.server.persist.grouppre


import im.actor.server.model.Peer
import im.actor.server.persist.dialog.UserDialogRepo.byPKC
import slick.dbio.Effect.Read
import slick.driver.PostgresDriver.api._
import slick.profile.SqlAction

case class PublicGroup(id: Int, typ: String, order: Int, hasChildrem: Boolean, parentId:Option[Int], accessHash: Long)
/**
 * Created by diego on 09/08/16.
 */
class PublicGroupTable(tag: Tag) extends Table[PublicGroup](tag, "public_group") {

  def id = column[Int]("id", O.PrimaryKey)

  def typ = column[String]("type")

  def position = column[Int]("position")

  def hasChildrem = column[Boolean]("has_childrem")

  def parentId = column[Option[Int]]("parent_id")

  def acessHash = column[Long]("access_hash")

  def * = (id, typ, position, hasChildrem, parentId, acessHash) <> ((PublicGroup.apply _).tupled, PublicGroup.unapply)

}

object PublicGroupRepo {

  val publicGroups = TableQuery[PublicGroupTable]

  def byIdPai(parentId: Option[Int]) = publicGroups.filter(_.parentId === parentId)

  def createOrUpdate(publicGroup: PublicGroup) = publicGroups.insertOrUpdate(publicGroup)

  def findByIdPai(parentId: Option[Int]): SqlAction[Seq[PublicGroup], NoStream, Read] = byIdPai(parentId).result

  def atualizaPossuiFilhos(parentId: Int, hasChildrem:Boolean) = {
    byIdPai(Some(parentId)).map(_.hasChildrem).update(hasChildrem)
  }

  def updateParent(groupId: Int, parentId:Int) = {
    publicGroups.filter(_.id === groupId).map(_.parentId).update(Some(parentId))
  }

  def possuiFilhos(parentId: Int): SqlAction[Boolean, NoStream, Read] = {
    byIdPai(Some(parentId)).exists.result
  }

  def findById(groupId:Int) =
    publicGroups.filter(_.id === groupId).result.headOption

  def delete(groupId:Int) =
    publicGroups.filter(_.id === groupId).delete

}
