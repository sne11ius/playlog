package service

import models.GeoCoord
import play.api.Play.current
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import play.api.libs.ws.WS
import play.api.Logger
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.libs.json.Json
import org.joda.time.DateTime

class GeoTrackerServiceImpl extends GeoTrackerService {

  implicit val coordReads: Reads[GeoCoord] = (
    (JsPath \ "latitude").read[Double] and
    (JsPath \ "longitude").read[Double] and
    (JsPath \ "altitude").read[Double] and
    (JsPath \ "accuracy").read[Float] and
    (JsPath \ "speed").read[Float] and
    (JsPath \ "time").read[DateTime]
  )(GeoCoord)

  def getLatest: Option[GeoCoord] = {
    current.configuration.getString("geotracker.url") match {
      case Some(text) => {
        var url = text + "/coordinates/latest"
        Logger.debug(s"Posting to $url")
        var apiKey = current.configuration.getString("geotraker.apikey").get
        Await.result(WS.url(url).withFollowRedirects(true).withHeaders("Content-Type" -> "application/x-www-form-urlencoded").post(s"apiKey=$apiKey").map { response =>
          Logger.debug(s"Response: ${response.body}")
          if (response.body.isEmpty()) {
            Logger.debug("Response is empty")
            None
          } else {
            val json = Json.parse(response.body)
            val coord = GeoCoord(
              (json \ "latitude").as[Double],
              (json \ "longitude").as[Double],
              (json \ "altitude").as[Double],
              (json \ "accuracy").as[Float],
              (json \ "speed").as[Float],
              new DateTime((json \ "time").as[Long])
            )
            Logger.debug(s"Got coords: $coord")
            Some(coord)
          }
        }, 10 seconds)
      }
      case None => None
    }
  }

}
