package im.actor.server.model.auth

case class BypassSmsNumbers(phone: Long, transactionHash: Option[String], code: Option[String])
