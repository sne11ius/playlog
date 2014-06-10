package controllers

import views._
import models.Post
import java.sql.Date
import models.database.Posts
import models._
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import play.api.libs.json._
import play.api.libs.functional.syntax._

object PostController extends Controller {
  
  val createPostForm: Form[Post] = Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText,
      "published" -> boolean
    )
    {
      (title, body, published) => Post(None, title, body, new DateTime, new DateTime, published, "anon user") 
    } 
    {
      post => Some(post.title, post.body, post.published)
    }
  )
  
  def form = Action {
    Ok(html.composepost.form(createPostForm));
  }
  
  def editForm = Action {
    val existingPost = Post(
      None, "faketitle", "fakebody", new DateTime, new DateTime, false, "anon user"
    )
    Ok(html.composepost.form(createPostForm.fill(existingPost)))
  }
  
  def showImport = Action {
    Ok(html.importposts.importPosts())
  }
  
  def importPosts = DBAction { implicit rs =>
    implicit val postReads: Reads[Post] = (
        Reads.pure(None) and
<<<<<<< HEAD
        (__ \ "title").read[String] and
        (__ \ "body").read[String] and
        (__ \ "date").read[DateTime] and
        (__ \ "date").read[DateTime] and
        Reads.pure(true) and
        Reads.pure("anon author")
=======
		(__ \ "title").read[String] and
		(__ \ "body").read[String] and
		(__ \ "date").read[DateTime] and
		(__ \ "date").read[DateTime] and
		Reads.pure(false) and
		Reads.pure("anon author")
    )(Post)
    val posts = Json.parse(Form("jsonPosts" -> text).bindFromRequest.get).as[List[Post]]
    posts.map(post => {
      println(post)
      Posts.insert(post)
    })
    Ok(html.importposts.importPosts())
  }
  
  def editPost = DBAction { implicit rs =>
    val postId = Form("postId" -> text).bindFromRequest.get.toLong
    val post = Posts.find(postId)
    Ok(html.composepost.form(createPostForm.fill(post)))
  }
  
  def submit = DBAction { implicit rs =>
    createPostForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.composepost.form(formWithErrors))
      },
      _ => {
        val post = createPostForm.bindFromRequest.get
        Posts.insert(post)
        Logger.debug("post added")
        Ok(html.composepost.summary(post))
      }
    )
  }
}
