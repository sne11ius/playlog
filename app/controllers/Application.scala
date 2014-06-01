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
import play.api.data._
import play.api.data.Forms._

object Application extends Controller {
  
  def index = DBAction { implicit rs =>
    Ok(views.html.index(Posts.findAll))
  }

  def removePost = DBAction { implicit rs => {
      val postId = Form("postId" -> text).bindFromRequest.get.toLong
      Posts.delete(postId)
      Redirect(routes.Application.index)
    }
  }
}
