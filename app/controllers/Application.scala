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

class Application @Inject() (implicit val env: Environment[User, CachedCookieAuthenticator])
    extends Controller with Silhouette[User, CachedCookieAuthenticator] {
  
  /*
  def setup = DBAction { implicit rs =>
    Ok(views.html.setup.step0())
  }
  */
  
  /*
  def index = DBAction { implicit rs =>  
    Ok(views.html.index(Posts.findAllPublished))
  }
  */
//  def index = SecuredAction.async { implicit request =>
  def index = UserAwareAction.async { implicit request =>
    DB.withSession { implicit session =>
      Future.successful(Ok(views.html.index(Posts.findAllPublished, request.identity)))
    }
  }
  
  def removeAll = SecuredAction.async { implicit request =>
    DB.withSession { implicit session =>
      Posts.deleteAll
      Future.successful(Redirect(routes.Application.index))
      //Ok(views.html.index(Posts.findAllPublished))
      //Future.successful(Ok(views.html.index(Posts.findAllPublished, request.identity)))
    }
  }
  
  def removePost = DBAction { implicit rs => {
      val postId = Form("postId" -> text).bindFromRequest.get.toLong
      Posts.delete(postId)
      Redirect(routes.Application.index)
    }
  }
}
