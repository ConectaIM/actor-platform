package im.actor.server.sms

import scala.util.Failure
import scala.util.parsing.json.{JSONFormat, JSONObject}
import scala.concurrent.{ExecutionContext, Future}
import java.util.Base64

import akka.actor.ActorSystem
import com.ning.http.client.Response
import com.typesafe.config.Config
import dispatch.{Http, url}

object PlivioClient{
  private val zenviaUrlBase = "https://api.plivo.com/v1/Account/$$ACCOUNT_ID$$/Message/"
  val DefaultSmsTemplate: String = "$$SYSTEM_NAME$$: Your activation code is $$CODE$$"
}

final class PlivioClient(config: Config)(implicit system: ActorSystem) {

  import PlivioClient._

  private val authId = config.getString("auth-id")
  private val authToken = config.getString("auth-token")
  private val srcNumber = config.getString("src-number")

  private lazy val http = new Http()

  system registerOnTermination http.shutdown()

  private implicit val ec: ExecutionContext = system.dispatcher

  def sendSmsCode(phoneNumber: Long, code: String, systemName: String, template: String): Future[Unit] = {
    postRequest(zenviaUrlBase.replace("$$ACCOUNT_ID$$", authId), Map(
      "sendSmsRequest" → JSONObject(Map("src"-> srcNumber,
        "dst" -> phoneNumber,
        "text" -> template.replace("$$SYSTEM_NAME$$", systemName).replace("$$CODE$$", code)))
    )) map { _ ⇒
      system.log.debug("Message sent via Plivio")
    }
  }

  private def postRequest(resourcePath: String, params: Map[String, Any]): Future[Response] = {
    val body = JSONObject(params).toString(JSONFormat.defaultFormatter)

    val resUrl = url(resourcePath)
    val request = (resUrl.POST.setContentType("application/json", "UTF-8").setHeader("Accept","application/json").setBody(body))
    val base64Key = Base64.getEncoder.encodeToString(s"${authId}:${authToken}".getBytes("UTF-8"))
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
