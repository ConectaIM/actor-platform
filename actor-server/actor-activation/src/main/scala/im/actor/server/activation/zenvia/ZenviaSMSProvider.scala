package im.actor.server.activation.zenvia


import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.util.FastFuture
import cats.data.Xor
import im.actor.config.ActorConfig
import im.actor.server.activation.common.ActivationStateActor.{ForgetSentCode, Send, SendAck}
import im.actor.server.activation.common._
import im.actor.server.db.DbExtension
import im.actor.server.sms.{ZenviaClient, ZenviaSmsEngine}
import im.actor.util.misc.PhoneNumberUtils.isTestPhone
import akka.pattern.ask
import akka.util.Timeout
import im.actor.server.model.AuthPhoneTransaction
import im.actor.server.persist.auth.{AuthTransactionRepo, BypassSmsNumbersRepo, GateAuthCodeRepo}

import scala.concurrent.Future
import scala.concurrent.duration._

private[activation] final class ZenviaSMSProvider(implicit system: ActorSystem)
  extends ActivationProvider with CommonAuthCodes {

  protected val activationConfig = ActivationConfig.load.getOrElse(throw new RuntimeException("Failed to load activation config"))

  private val log = Logging(system, getClass)
  protected val db = DbExtension(system).db

  protected implicit val ec = system.dispatcher

  private val zenviaClient = new ZenviaClient(ActorConfig.load().getConfig("services.zenvia"))
  private val smsEngine = new ZenviaSmsEngine(zenviaClient)

  private implicit val timeout = Timeout(20.seconds)

  private val smsStateActor = system.actorOf(ActivationStateActor.props[Long, SmsCode](
    repeatLimit = activationConfig.repeatLimit,
    sendAction = (code: SmsCode) ⇒ smsEngine.sendCode(code.phone, code.systemName, code.code),
    id = (code: SmsCode) ⇒ code.phone
  ), "zenvia-sms-state")

  override def send(txHash: String, code: Code): Future[Xor[CodeFailure, Unit]] = code match {
    case s: SmsCode ⇒
      for {
        skipPhone ← db.run(BypassSmsNumbersRepo.filterByNumber(s.phone))
        resp ← skipPhone match {
          case Some(phoneNumber) ⇒ {
            for (_ ← db.run(BypassSmsNumbersRepo.createOrUpdate(s.phone, txHash))) yield Xor.right(())
          }
          case None ⇒ {
            if (isTestPhone(s.phone))
              FastFuture.successful(Xor.right(()))
            else
              for {
                res <- (smsStateActor ? Send(code)).mapTo[SendAck]
                _ ← createAuthCodeIfNeeded(res.result, txHash, code.code)
              } yield res.result
          }
        }
      } yield resp
    case other ⇒ throw new RuntimeException(s"This provider can't handle code of type: ${other.getClass}")
  }

  override def validate(txHash: String, code: String): Future[ValidationResponse] = {
    for {
      skipPhone ← db.run(BypassSmsNumbersRepo.filterByHash(txHash))
      retorno ← skipPhone match {
        case Some(phone) ⇒ {
          if (phone.code.getOrElse(false).equals(code)) {
            FastFuture.successful(Validated)
          } else {
            FastFuture.successful(InvalidCode)
          }
        }
        case None ⇒ {
          super.validate(txHash, code)
        }
      }
    } yield retorno
  }

  override def cleanup(txHash: String): Future[Unit] = {
    for {
      ac ← db.run(AuthTransactionRepo.findChildren(txHash))
      _ = ac match {
        case Some(x: AuthPhoneTransaction) ⇒
          smsStateActor ! ForgetSentCode.phone(x.phoneNumber)
        case _ ⇒
      }
      _ ← deleteAuthCode(txHash)
    } yield ()
  }
}
