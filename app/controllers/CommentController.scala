package controllers

import play.api._
import play.api.mvc._
import views._
import play.api.db.slick.DBAction
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
import service.PostService
import forms.CommentForm
import org.joda.time.DateTime
import java.util.UUID

class CommentController @Inject() (postService: PostService, implicit val env: Environment[User, CachedCookieAuthenticator])
    extends Controller with Silhouette[User, CachedCookieAuthenticator] {
  
  def addComment = SecuredAction.async { implicit request =>
    CommentForm.form.bindFromRequest.fold (
      form => {
        Logger.debug("Bad request")
        Future.successful(BadRequest(views.html.index(FeedConfig, postService.findAllPublished(None), Some(request.identity))))
      },
      data => {
        val author = request.identity
        val title = data.title
        val body = data.body
        val comment = Comment(None, title, body, new DateTime(), new DateTime(), author)
        val postId = UUID.fromString(Form("postId" -> text).bindFromRequest.get)
        
        Logger.debug("Now adding the post comment: " + comment)
        Logger.debug("To post #" + postId)
        postService.addComment(postId, comment)
      }
    )
    Future.successful(Redirect(routes.Application.index(None)))
  }
}
