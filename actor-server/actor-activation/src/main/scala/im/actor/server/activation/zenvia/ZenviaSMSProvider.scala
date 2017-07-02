package im.actor.server.activation.zenvia


import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.util.FastFuture
import cats.data.Xor
import im.actor.config.ActorConfig
import im.actor.server.activation.common.ActivationStateActor.{Send, SendAck}
import im.actor.server.activation.common._
import im.actor.server.db.{DbExtension}
import im.actor.server.sms.{ZenviaClient, ZenviaSmsEngine}
import im.actor.util.misc.PhoneNumberUtils.isTestPhone

import akka.pattern.ask
import im.actor.server.persist.auth.{BypassSmsNumbersRepo, GateAuthCodeRepo}

import scala.concurrent._


private[activation] final class LotericaSmsProvider(implicit system: ActorSystem)
  extends ActivationProvider with CommonAuthCodes {

  protected val activationConfig = ActivationConfig.load.getOrElse(throw new RuntimeException("Failed to load activation config"))

  private val log = Logging(system, getClass)
  protected val db = DbExtension(system).db

  protected implicit val ec = system.dispatcher

  private val zenviaClient = new ZenviaClient(ActorConfig.load().getConfig("services.zenvia"))
  private val smsEngine = new ZenviaSmsEngine(zenviaClient)


  private val smsStateActor = system.actorOf(ActivationStateActor.props[Long, SmsCode](
    repeatLimit = activationConfig.repeatLimit,
    sendAction = (code: SmsCode) ⇒ smsEngine.sendCode(code.phone, code.systemName, code.code),
    id = (code: SmsCode) ⇒ code.phone
  ), "zenvia-sms-state")


  override def send(txHash: String, code: Code): Future[Xor[CodeFailure, Unit]] = code match {
    case s: SmsCode ⇒
      for {
        resp ← if (isTestPhone(s.phone))
          FastFuture.successful(Xor.right(()))
        else
          (smsStateActor ? Send(code)).mapTo[SendAck].map(_.result)
        _ ← createAuthCodeIfNeeded(resp, txHash, code.code)
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
          validateSmsCode(txHash, code)
        }
      }
    } yield retorno
  }

  def validateSmsCode(txHash: String, code: String): Future[ValidationResponse] = {
    for {
      optCodeHash ← db.run(GateAuthCodeRepo.find(txHash))
      validObject ← optCodeHash map { codeHash ⇒
        if (codeHash.codeHash.equals(code)) {
          FastFuture.successful(Validated)
        } else {
          FastFuture.successful(InvalidCode)
        }
      } getOrElse FastFuture.successful(InvalidHash)
    } yield validObject
  }

  override def cleanup(txHash: String): Future[Unit] = db.run(GateAuthCodeRepo.delete(txHash)).map(_ ⇒ ())
}
