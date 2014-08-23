package service;

import play.api.Play
import play.Logger
import play.api.Play.current
import play.api.libs.ws._
import scala.concurrent.ExecutionContext.Implicits.global

import models.YoSubscriber
import com.google.inject.Inject
import models.daos.YoSubscriberDAO

class YoServiceImpl @Inject() (yoSubscriberDAO: YoSubscriberDAO) extends YoService {
  
  def addSubscriber(subscriber: YoSubscriber) {
    yoSubscriberDAO.add(subscriber)
  }
  
  def findAll() : List[YoSubscriber] = {
    yoSubscriberDAO.findAll
  }
  
  def sendYoToSubscribers(permalink: String) {
    val token = Play.configuration.getString("nu.wasis.yotoken").get
    val url = "http://api.justyo.co/yoall/"
    val futureResponse = WS.url(url).post(Map(
      "api_token" -> Seq(token),
      "link" -> Seq(permalink)
    ))
    futureResponse.recover {
      case e: Exception => {
        val exceptionData = Map("error" -> Seq(e.getMessage))
        Logger.error("Error sending yo: " + exceptionData)
      }
    }
    futureResponse.andThen {
      case _ => Logger.info("Yo sent. Permalink: " + permalink)
    }
  }
  
}
