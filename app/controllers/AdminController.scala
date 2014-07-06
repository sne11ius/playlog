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
import javax.inject.Inject
import com.mohiva.play.silhouette.core.Environment
import com.mohiva.play.silhouette.contrib.services.CachedCookieAuthenticator
import com.mohiva.play.silhouette.core.Silhouette
import scala.concurrent.Future
import play.api.Play.current
import models.database.AdminIdentifiers
import com.mohiva.play.silhouette.core.exceptions.AccessDeniedException

class AdminController @Inject() (implicit val env: Environment[User, CachedCookieAuthenticator])
    extends Controller with Silhouette[User, CachedCookieAuthenticator] {
  
  def index = SecuredAction.async { implicit request =>
    DB.withSession { implicit session =>
      if (AdminIdentifiers.findByUserId(request.identity.userID).isDefined) {
        Future.successful(Ok(views.html.admin(request.identity)))
      } else {
        throw new AccessDeniedException("You are not an admin!!!!")
      }
    }
  }
}
