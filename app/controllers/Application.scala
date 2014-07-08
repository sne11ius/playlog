package controllers

import play.api._
import play.api.mvc._
import views._
import play.api.db.slick.DBAction
import models.database.Posts
import models._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import javax.inject.Inject
import com.mohiva.play.silhouette.core.Environment
import com.mohiva.play.silhouette.contrib.services.CachedCookieAuthenticator
import com.mohiva.play.silhouette.core.Silhouette
import scala.concurrent.Future
import play.api.Play.current
import models.database.AdminIdentifiers
import service.UserService

class Application @Inject() (userService: UserService, implicit val env: Environment[User, CachedCookieAuthenticator])
    extends Controller with Silhouette[User, CachedCookieAuthenticator] {
  
  def index = UserAwareAction.async { implicit request =>
    DB.withSession { implicit session =>
      if (AdminIdentifiers.findAll.isEmpty) {
        Future.successful(Ok(views.html.plain()))
      } else {
    	  Future.successful(Ok(views.html.index(Posts.findAllPublished, userService.findAll, request.identity)))
      }
    }
  }
  
  def removeAll = SecuredAction.async { implicit request =>
    DB.withSession { implicit session =>
      Posts.deleteAll
      Future.successful(Redirect(routes.Application.index))
    }
  }
  
  def removePost = DBAction { implicit rs => {
      val postId = Form("postId" -> text).bindFromRequest.get.toLong
      Posts.delete(postId)
      Redirect(routes.Application.index)
    }
  }
}
