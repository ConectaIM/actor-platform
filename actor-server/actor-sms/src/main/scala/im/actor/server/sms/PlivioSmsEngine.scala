package im.actor.server.sms

import scala.concurrent.Future

/**
  * Created by diego on 01/07/17.
  */
final class PlivioSmsEngine(plivioClient: PlivioClient) extends AuthSmsEngine{
  override def sendCode(phoneNumber: Long, systemName: String, code: String): Future[Unit] = plivioClient.sendSmsCode(phoneNumber, code, systemName, PlivioClient.DefaultSmsTemplate)
}
