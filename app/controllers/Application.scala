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
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeFormat
import org.joda.time.DateTimeZone
import java.util.UUID

class Application @Inject() (userService: UserService, postService: PostService, implicit val env: Environment[User, CachedCookieAuthenticator])
    extends Controller with Silhouette[User, CachedCookieAuthenticator] {
  
  def index(inTitle: Option[String], start: Option[Int], numItems: Option[Int]) = UserAwareAction.async { implicit request =>
    DB.withSession { implicit session =>
      if (AdminIdentifiers.findAll.isEmpty) {
        Future.successful(Ok(views.html.plain()))
      } else if (start.isDefined && numItems.isDefined) {
        //Logger.debug("Paging: start=" + start.get + " numItems=" + numItems.get)
        val posts = postService.findAllPublished(start.get, numItems.get)
        val numPosts = postService.countAllPublished
        val numPages = ((numPosts:Float) / numItems.get).ceil.toInt
    	Future.successful(Ok(views.html.index(FeedConfig, FeedConfig.title, posts, request.identity, Some(PaginationInfo(start.get, numItems.get, numPages)))))
      } else if (inTitle.isDefined) {
        val posts = postService.findAllPublished(inTitle)
        val title = if (1 == posts.length) posts(0).title.replaceAll("\\<.*?\\>", "").replaceAll("\\&.*?\\;", "") + " - wasis.nu/mit/blog" else FeedConfig.title
    	Future.successful(Ok(views.html.index(FeedConfig, title, posts, request.identity)))
      } else {
        val start = 0
        val numItems = 5
        val posts = postService.findAllPublished(start, numItems)
        val numPosts = postService.countAllPublished
        val numPages = ((numPosts:Float) / numItems).ceil.toInt
    	Future.successful(Ok(views.html.index(FeedConfig, FeedConfig.title, posts, request.identity, Some(PaginationInfo(start, numItems, numPages)))))
      }
    }
  }
  
  def about = Action {
    Ok(views.html.about())
  }
  
  def singlePost(dateString: String, title: String) = UserAwareAction.async { implicit request =>
    DB.withSession { implicit session =>
      val date = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateString).withZone(DateTimeZone.UTC).withHourOfDay(0)
      val singlePost = postService.findSinglePost(date, title)
      val pageTitle = singlePost(0).title.replaceAll("\\<.*?\\>", "").replaceAll("\\&.*?\\;", "") + " - wasis.nu/mit/blog"
      Future.successful(Ok(views.html.index(FeedConfig, pageTitle, postService.findSinglePost(date, title), request.identity)))
    }
  }
  
  def removeAll = SecuredAction.async { implicit request =>
    DB.withSession { implicit session =>
      postService.deleteAll
      Future.successful(Redirect(routes.Application.index(None, None, None)))
    }
  }
  
  def removePost = DBAction { implicit rs => {
      val postId = UUID.fromString(Form("postId" -> text).bindFromRequest.get)
      postService.delete(postId)
      Redirect(routes.Application.index(None, None, None))
    }
  }
  
    /**
   * Handles the Sign Up action.
   *
   * @return The result to display.
   */
  def signUp = UserAwareAction.async { implicit request =>
    request.identity match {
      case Some(user) => Future.successful(Redirect(routes.Application.index(None, None, None)))
      case None => Future.successful(Ok(views.html.signUp(SignUpForm.form)))
    }
  }
}
