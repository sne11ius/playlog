package controllers

import play.api._
import play.api.mvc._
import views._
import play.api.db.slick.DBAction
import models.database.Posts
import models._
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._

object Application extends Controller {
  
  def index = DBAction { implicit rs =>
    Ok(views.html.index(Posts.findAll))
  }
}
