package im.actor.server.sms


import java.util.Base64

import akka.actor.ActorSystem
import com.ning.http.client.Response
import com.typesafe.config.Config
import dispatch.{Http, url}
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.javadsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.util.FastFuture
import cats.data.Xor
import im.actor.server.activation.common._
import spray.httpx.marshalling._
import spray.httpx.unmarshalling._

import scala.concurrent.Future
import scala.util.Failure
import scala.util.parsing.json.{JSONFormat, JSONObject}


object ZenviaClient{
  private val zenviaUrlBase = "https://api-rest.zenvia360.com.br/services/send-sms"
  val DefaultSmsTemplate: String = "$$SYSTEM_NAME$$: Seu codigo de ativação é $$CODE$$"
}

final class ZenviaClient(config: Config)(implicit system: ActorSystem) {

  import ZenviaClient._

  private val base64Key = Base64.getEncoder.encodeToString(s"${config.getString("customer-id")}:${config.getString("api-key")}".getBytes("utf-8"))

  private lazy val http = new Http()

  def sendSmsCode(phoneNumber: Long, code: String, systemName: String, template: String): Future[Unit] = {
    postRequest(zenviaUrlBase, Map(
      "sendSmsRequest" → Map("to" -> phoneNumber, "msg" -> template.replace("$$SYSTEM_NAME$$", systemName).replace("$$CODE$$", code))
    )) map { _ ⇒
      system.log.debug("Message sent via Zenvia")
    }
  }

  private def postRequest(resourcePath: String, params: Map[String, Map[String, Any]]): Future[Response] = {
    val body = JSONObject(params).toString(JSONFormat.defaultFormatter)

    val resUrl = url(resourcePath)
    val request = (resUrl.POST.setContentType("application/json", "charset=utf-8").setBody(body))

    val requestWithAuth = request
      .addHeader("Authorization", base64Key)

    http(requestWithAuth).map { resp ⇒
      if (resp.getStatusCode < 199 || resp.getStatusCode > 299) {
        throw new RuntimeException(s"Response has code ${resp.getStatusCode}. [${resp.getResponseBody}]")
      } else {
        resp
      }
    } andThen {
      case Failure(e) ⇒
        system.log.error(e, "Failed to make request to telesign")
    }
  }

}
