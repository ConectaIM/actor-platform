package im.actor.server.sms

import scala.util.Failure
import scala.util.parsing.json.{JSONFormat, JSONObject}
import scala.concurrent.{ExecutionContext, Future}
import java.util.Base64

import akka.actor.ActorSystem
import com.ning.http.client.Response
import com.typesafe.config.Config
import dispatch.{Http, url}

object ZenviaClient{
  private val zenviaUrlBase = "https://api-rest.zenvia360.com.br/services/send-sms"
  val DefaultSmsTemplate: String = "$$SYSTEM_NAME$$: Seu codigo de ativação é $$CODE$$"
}

final class ZenviaClient(config: Config)(implicit system: ActorSystem) {

  import ZenviaClient._

  private val customerId = config.getString("customer-id")
  private val apiKey = config.getString("api-key")

  private lazy val http = new Http()

  system registerOnTermination http.shutdown()

  private implicit val ec: ExecutionContext = system.dispatcher

  def sendSmsCode(phoneNumber: Long, code: String, systemName: String, template: String): Future[Unit] = {
    postRequest(zenviaUrlBase, Map(
      "sendSmsRequest" → JSONObject(Map("to" -> phoneNumber, "msg" -> template.replace("$$SYSTEM_NAME$$", systemName).replace("$$CODE$$", code)))
    )) map { _ ⇒
      system.log.debug("Message sent via Zenvia")
    }
  }

  private def postRequest(resourcePath: String, params: Map[String, Any]): Future[Response] = {
    val body = JSONObject(params).toString(JSONFormat.defaultFormatter)

    val resUrl = url(resourcePath)
    val request = (resUrl.POST.setContentType("application/json", "UTF-8").setHeader("Accept","application/json").setBody(body))
    val base64Key = Base64.getEncoder.encodeToString(s"${customerId}:${apiKey}".getBytes("UTF-8"))
    val requestWithAuth = request.addHeader("Authorization", base64Key)

    http(requestWithAuth).map { resp ⇒
      if (resp.getStatusCode < 199 || resp.getStatusCode > 299) {
        throw new RuntimeException(s"Response has code ${resp.getStatusCode}. [${resp.getResponseBody}]")
      } else {
        resp
      }
    } andThen {
      case Failure(e) ⇒ {
        system.log.error(e, "Failed to make request to zenvia")
      }
    }
  }

}
