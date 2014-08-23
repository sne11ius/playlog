package controllers;

import play.api._
import play.api.mvc._
import play.api.Play.current
import javax.inject.Inject
import java.util.UUID
import service.YoService
import models.YoSubscriber

class YoApiEndpointController @Inject() (yoService: YoService) extends Controller {
  
  def addUser(username: String) = Action {
    yoService.addSubscriber(YoSubscriber(UUID.randomUUID(), username))
    Ok
  }
  
}
