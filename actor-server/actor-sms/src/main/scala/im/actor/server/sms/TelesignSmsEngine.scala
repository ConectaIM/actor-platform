package im.actor.server.sms

import scala.concurrent.Future

final class TelesignSmsEngine(telesignClient: TelesignClient) extends AuthSmsEngine {
  override def sendCode(phoneNumber: Long, systemName:String, code: String): Future[Unit] = telesignClient.sendSmsCode(phoneNumber, code, systemName, TelesignClient.DefaultSmsTemplate)
}
