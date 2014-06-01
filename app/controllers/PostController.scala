package controllers

import views._
import models.Post
import java.sql.Date
import models.database.Posts
import play.api.db.slick.DBAction
import play.api.db.slick.Config.driver.simple._
import models._
import play.api._
import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.BodyParsers._
import play.api.libs.json.Json
import play.api.libs.json.Json._

object PostController extends Controller {
  
  val signupForm: Form[Post] = Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText,
      "published" -> boolean
    )
    {
      (title, body, published) => Post(None, title, body, new Date(0), new Date(0), published, "anon user") 
    } 
    {
      post => Some(post.title, post.body, post.published)
    }
  )
  
  def form = Action {
    Ok(html.composepost.form(signupForm));
  }
  
  def editForm = Action {
    val existingPost = Post(
      None, "faketitle", "fakebody", new Date(0), new Date(0), false, "anon user"
    )
    Ok(html.composepost.form(signupForm.fill(existingPost)))
  }
  
  def submit = DBAction { implicit rs =>
    signupForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.composepost.form(formWithErrors))
      },
      postData => {
        val post = signupForm.bindFromRequest.get
        Posts.insert(post)
        Ok(html.composepost.summary(post))
      }
    )
  }
}
