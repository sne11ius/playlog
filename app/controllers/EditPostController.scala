package controllers

import views._
import models.Post
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.Logger
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import org.joda.time.DateTime
import models.database._
import models._
import service._
import com.mohiva.play.silhouette.core.Environment
import com.mohiva.play.silhouette.contrib.services.CachedCookieAuthenticator
import com.mohiva.play.silhouette.core.Silhouette
import javax.inject.Inject
import play.api.Play.current
import scala.concurrent.Future
import com.mohiva.play.silhouette.core.exceptions.AccessDeniedException
import java.util.UUID

class EditPostController @Inject() (userService: UserService, postService: PostService, implicit val env: Environment[User, CachedCookieAuthenticator])
    extends Controller with Silhouette[User, CachedCookieAuthenticator] {
  
  val editPostForm: Form[Post] = Form(
    // WhyTF can I not have my postId as a Long?
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText,
      "published" -> boolean
    )
    {
      (title, body, published) => Post(UUID.randomUUID(), title, body, null, null, published, null, List())
    } 
    {
      post => Some(post.title, post.body, post.published)
    }
  )
  
  def listUnpublishedPosts() = SecuredAction.async { implicit request =>
    DB.withSession { implicit session =>
      if (request.identity.isAdmin) {
        Future.successful(Ok(html.listPosts(postService.findAllUnpublished, request.identity)))
      } else {
        throw new AccessDeniedException("You are not an admin!!!!")
      }
    }
  }
  
  def editExistingPost(postId: UUID) = SecuredAction.async { implicit request =>
    DB.withSession { implicit session =>
      if (request.identity.isAdmin) {
        Logger.debug("Editing post by id: " + postId)
        val post = postService.find(postId);
        Future.successful(Ok(html.editPost(editPostForm.fill(post), post)))
      } else {
        throw new AccessDeniedException("You are not an admin!!!!")
      }
    }
  }
  
  def updateExistingPost(postId: UUID) = SecuredAction.async { implicit request =>
    DB.withSession { implicit session =>
      if (!request.identity.isAdmin) {
        throw new AccessDeniedException("You are not an admin!!!!")
      }
      editPostForm.bindFromRequest.fold(
        error => {
          Logger.error("the fuck:")
          Logger.error(error.toString)
        },
        editedPost => {
          Logger.debug("Updating post by id: " + postId)
          val existingPost = postService.find(postId)
    	  val mergedPost = Post(
    	    postId,
    	    editedPost.title,
    	    editedPost.body,
    	    existingPost.created,
    	    new DateTime,
    	    editedPost.published,
    	    request.identity,
    	    existingPost.comments
    	  )
    	  postService.update(mergedPost)
          Ok(html.editPost(editPostForm.fill(mergedPost), mergedPost))
        }
      )
      val existingPost = postService.find(postId)
      Future.successful(Ok(html.editPost(editPostForm.fill(existingPost), existingPost)))
    }
  }
}
