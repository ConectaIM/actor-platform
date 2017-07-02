package im.actor.server.sms
import scala.concurrent.Future

/**
  * Created by diego on 01/07/17.
  */
final class ZenviaSmsEngine(zenviaClient: ZenviaClient) extends AuthSmsEngine{
  override def sendCode(phoneNumber: Long, systemName: String, code: String): Future[Unit] = zenviaClient.sendSmsCode(phoneNumber, code, systemName, ZenviaClient.DefaultSmsTemplate)
}
