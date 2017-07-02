package im.actor.server.persist.auth

import im.actor.server.model.auth.BypassSmsNumbers
import slick.driver.PostgresDriver.api._

/**
  * Created by diego on 23/10/16.
  */
class BypassSmsNumbersTable(tag: Tag) extends Table[BypassSmsNumbers](tag, "bypass_sms_numbers") {
  def phone = column[Long]("phone", O.PrimaryKey)

  def transactionHash = column[String]("transaction_hash")

  def code = column[String]("code")

  def * = (phone, transactionHash.?, code.?) <> ((BypassSmsNumbers.apply _).tupled, BypassSmsNumbers.unapply)

}

object BypassSmsNumbersRepo {

  private val numbers = TableQuery[BypassSmsNumbersTable]

  def filterByNumber(phoneNumber: Long): DBIO[Option[BypassSmsNumbers]] = {
    numbers.filter(_.phone === phoneNumber).result.headOption
  }

  def filterByHash(transactionHash: String): DBIO[Option[BypassSmsNumbers]] = {
    numbers.filter(_.transactionHash === transactionHash).result.headOption
  }

  def createOrUpdate(phoneNumber: Long, transactionHash: String) = {
    numbers.map(n ⇒ (n.phone, n.transactionHash)).insertOrUpdate((phoneNumber, transactionHash))
  }

}