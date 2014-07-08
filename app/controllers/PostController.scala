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
import javax.inject.Inject
import com.mohiva.play.silhouette.core.Environment
import com.mohiva.play.silhouette.contrib.services.CachedCookieAuthenticator
import com.mohiva.play.silhouette.core.Silhouette
import scala.concurrent.Future
import models.daos.UserDAO
import scala.concurrent.ExecutionContext.Implicits.global

class PostController @Inject() (userDAO: UserDAO, implicit val env: Environment[User, CachedCookieAuthenticator])
    extends Controller with Silhouette[User, CachedCookieAuthenticator] {
  
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
  
  def importPosts = UserAwareAction.async { implicit request =>
    implicit val postReads: Reads[Post] = (
        Reads.pure(None) and
        (__ \ "title").read[String] and
        (__ \ "body").read[String] and
        (__ \ "date").read[DateTime] and
        (__ \ "date").read[DateTime] and
        Reads.pure(true) and
        Reads.pure(request.identity.get.userID.toString())
    )(Post)
    val posts = Json.parse(Form("jsonPosts" -> text).bindFromRequest.get).as[List[Post]]
    
    DB withSession { implicit session =>
      posts.map(post => {
        println(post)
        Posts.insert(post)
      })
    }
    Future.successful(Ok(html.importposts.importPosts()))
  }
  
  def editPost = UserAwareAction.async { implicit request =>
    DB withSession { implicit session =>
      val postId = Form("postId" -> text).bindFromRequest.get.toLong
      val post = Posts.find(postId)
      Future.successful(Ok(html.composepost.form(createPostForm.fill(post))))
    }
  }
  
  def submit = SecuredAction.async { implicit request =>
    createPostForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.composepost.form(formWithErrors)))
      },
      _ => {
        DB withSession { implicit session =>
          val p = createPostForm.bindFromRequest.get
          Logger.debug("Did bind post from form")
          userDAO.find(request.identity.userID) andThen {
            case user => {
              Logger.debug("Found user")
              if (user.get.get.isAdmin) {
                Logger.debug("User is admin")
                val post = Post(None, p.title, p.body, p.created, p.edited, p.published, user.get.get.userID.toString())
                Posts.insert(post)
                Logger.debug("post added")
                Future.successful(Redirect(routes.Application.index))
              }
            }
          }
          Future.successful(Redirect(routes.Application.index))
        }
      }
    )
  }
}
