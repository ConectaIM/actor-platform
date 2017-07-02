package im.actor.server.sms

import scala.concurrent.Future

trait AuthSmsEngine {
  def sendCode(phoneNumber: Long, systemName:String, code: String): Future[Unit]
}