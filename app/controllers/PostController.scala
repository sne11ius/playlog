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
import org.joda.time.DateTime
import play.api.libs.json.JsValue

object PostController extends Controller {
  
  val signupForm: Form[Post] = Form(
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
    Ok(html.composepost.form(signupForm));
  }
  
  def editForm = Action {
    val existingPost = Post(
      None, "faketitle", "fakebody", new DateTime, new DateTime, false, "anon user"
    )
    Ok(html.composepost.form(signupForm.fill(existingPost)))
  }
  
  def showImport = Action {
    Ok(html.importposts.importPosts())
  }
  
  def importPosts = DBAction { implicit rs =>
    val json = Json.parse(Form("jsonPosts" -> text).bindFromRequest.get).as[List[JsValue]]
    json.map(post => {
      println("=======================================================")
      val title = (post \ "title").asOpt[String]
      val body = (post \ "body").asOpt[String]
      val id = (post \ "id").asOpt[String]
      val dateCreated = (post \ "dateCreated").asOpt[Long]
      val dateUpdated = (post \ "dateUpdated").asOpt[Long]
      def print(name: String, content: Option[AnyRef]) = {
        println(name + ": " + content)
      }
      def printLong(name: String, content: Option[Long]) = {
        println(name + ": " + content)
      }
      print("id", id)
      print("title", title)
      print("body", body)
      printLong("dateCreated", dateCreated)
      printLong("dateUpdated", dateUpdated)
    })
    Ok(html.importposts.importPosts())
  }
  
  def submit = DBAction { implicit rs =>
    signupForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.composepost.form(formWithErrors))
      },
      _ => {
        val post = signupForm.bindFromRequest.get
        Posts.insert(post)
        Ok(html.composepost.summary(post))
      }
    )
  }
}
