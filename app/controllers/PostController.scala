package controllers

import views._
import models.Post
import java.sql.Date
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
import service.PostService
import java.util.UUID

class PostController @Inject() (userDAO: UserDAO, postService: PostService, implicit val env: Environment[User, CachedCookieAuthenticator])
  extends Controller with Silhouette[User, CachedCookieAuthenticator] {

  val createPostForm: Form[Post] = Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText,
      "published" -> boolean) {
        (title, body, published) => Post(UUID.randomUUID(), title, body, new DateTime, new DateTime, published, UserStub, List())
      } {
        post => Some(post.title, post.body, post.published)
      })

  def form = Action {
    Ok(html.composepost.form(createPostForm));
  }

  def editForm = Action {
    val existingPost = Post(
      UUID.randomUUID(), "faketitle", "fakebody", new DateTime, new DateTime, false, UserStub, List()
    )
    Ok(html.composepost.form(createPostForm.fill(existingPost)))
  }

  def showImport = Action {
    Ok(html.importposts.importPosts())
  }

  def importPosts = UserAwareAction.async { implicit request =>
    implicit val postReads: Reads[Post] = (
      Reads.pure(UUID.randomUUID()) and
      (__ \ "title").read[String] and
      (__ \ "body").read[String] and
      (__ \ "date").read[DateTime] and
      (__ \ "date").read[DateTime] and
      Reads.pure(true) and
      Reads.pure(request.identity.get) and
      Reads.pure(List()))(Post)
    val postsToAdd = Json.parse(Form("jsonPosts" -> text).bindFromRequest.get).as[List[Post]]

    Logger.debug("Importing...")
    DB withSession { implicit session =>
      postsToAdd.map(post => {
        println(post)
        postService.insert(post)
      })
    }
    Logger.debug("...done")
    Future.successful(Redirect(routes.Application.index(None)))
  }

  def editPost = UserAwareAction.async { implicit request =>
    DB withSession { implicit session =>
      val postId = UUID.fromString(Form("postId" -> text).bindFromRequest.get)
      val post = postService.find(postId)
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
                val post = Post(UUID.randomUUID(), p.title, p.body, p.created, p.edited, p.published, user.get.get, List())
                postService.insert(post)
                Logger.debug("post added")
                Future.successful(Redirect(routes.Application.index(None)))
              }
            }
          }
          Future.successful(Redirect(routes.Application.index(None)))
        }
      })
  }
}
