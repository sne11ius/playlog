package controllers

import play.api._
import play.api.mvc._
import views._
import play.api.db.slick.DBAction
import models._
import service.PostService
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
import forms._

class Application @Inject() (userService: UserService, postService: PostService, implicit val env: Environment[User, CachedCookieAuthenticator])
    extends Controller with Silhouette[User, CachedCookieAuthenticator] {
  
  def index(inTitle: Option[String]) = UserAwareAction.async { implicit request =>
    DB.withSession { implicit session =>
      if (AdminIdentifiers.findAll.isEmpty) {
        Future.successful(Ok(views.html.plain()))
      } else {
    	  Future.successful(Ok(views.html.index(postService.findAllPublished(inTitle), request.identity)))
      }
    }
  }
  
  def removeAll = SecuredAction.async { implicit request =>
    DB.withSession { implicit session =>
      postService.deleteAll
      Future.successful(Redirect(routes.Application.index(None)))
    }
  }
  
  def removePost = DBAction { implicit rs => {
      val postId = Form("postId" -> text).bindFromRequest.get.toLong
      postService.delete(postId)
      Redirect(routes.Application.index(None))
    }
  }
  
    /**
   * Handles the Sign Up action.
   *
   * @return The result to display.
   */
  def signUp = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.Application.index(None)))
      case None => Future.successful(Ok(views.html.signUp(SignUpForm.form)))
    }
  }
}
