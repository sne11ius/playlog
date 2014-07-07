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
import models.database.Posts
import models.Post

object EditPostController extends Controller {
  
  val editPostForm: Form[Post] = Form(
    // WhyTF can I not have my postId as a Long?
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText,
      "published" -> boolean
    )
    {
      (title, body, published) => Post(None, title, body, null, null, published, null) 
    } 
    {
      post => Some(post.title, post.body, post.published)
    }
  )
  
  def editExistingPost(postId: Long) = DBAction { implicit rs =>
    Logger.debug("Editing post by id: " + postId)
    val post = Posts.find(postId);
    Ok(html.editPost(editPostForm.fill(post), post))
  }
  
  def updateExistingPost(postId: Long) = DBAction { implicit rs =>
    editPostForm.bindFromRequest.fold(
      error => {
        Logger.error("the fuck:")
        Logger.error(error.toString)
      },
      editedPost => {
        Logger.debug("Updating post by id: " + postId)
        val existingPost = Posts.find(postId)
	    val mergedPost = Post(
	      Some(postId),
	      editedPost.title,
	      editedPost.body,
	      existingPost.created,
	      new DateTime,
	      editedPost.published,
	      existingPost.authorId
	    )
	    Posts.update(mergedPost)
        Ok(html.editPost(editPostForm.fill(mergedPost), mergedPost))
      }
    )
    val existingPost = Posts.find(postId)
    Ok(html.editPost(editPostForm.fill(existingPost), existingPost))
  }
}
