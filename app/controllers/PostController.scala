package controllers

import views._
import models.Post
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
import com.mohiva.play.silhouette.core.exceptions.AccessDeniedException
import service.YoService

class PostController @Inject() (userDAO: UserDAO, postService: PostService, yoService: YoService, implicit val env: Environment[User, CachedCookieAuthenticator])
  extends Controller with Silhouette[User, CachedCookieAuthenticator] {

  val createPostForm: Form[Post] = Form(
    mapping(
      "title" -> nonEmptyText,
      "body" -> nonEmptyText,
      "published" -> boolean
    ) {
        (title, body, published) => Post(UUID.randomUUID(), title, body, new DateTime, new DateTime, published, UserStub, List())
      } {
        post => Some(post.title, post.body, post.published)
      }
    )

  def form = SecuredAction { implicit request =>
    DB.withSession { implicit session =>
      if (request.identity.isAdmin) {
        Ok(html.composepost.form(createPostForm, request.identity));
      } else {
        throw new AccessDeniedException("You are not an admin!!!!")
      }
    }
  }

  def editForm = SecuredAction { implicit request =>
    DB.withSession { implicit session =>
      if (request.identity.isAdmin) {
	    val existingPost = Post(
	      UUID.randomUUID(), "faketitle", "fakebody", new DateTime, new DateTime, false, UserStub, List()
	    )
	    Ok(html.composepost.form(createPostForm.fill(existingPost), request.identity))
      } else {
        throw new AccessDeniedException("You are not an admin!!!!")
      }
    }
  }

  def showImport = SecuredAction { implicit request =>
    DB.withSession { implicit session =>
      if (request.identity.isAdmin) {
    	Ok(html.importposts.importPosts())
      } else {
        throw new AccessDeniedException("You are not an admin!!!!")
      }
    }
  }

  def importPosts = SecuredAction.async { implicit request =>
    DB.withSession { implicit session =>
      if (request.identity.isAdmin) {
	    implicit val postReads: Reads[Post] = (
	      Reads.pure(UUID.randomUUID()) and
	      (__ \ "title").read[String] and
	      (__ \ "body").read[String] and
	      (__ \ "date").read[DateTime] and
	      (__ \ "date").read[DateTime] and
	      Reads.pure(true) and
	      Reads.pure(request.identity) and
	      Reads.pure(List())
	    )(Post)
	    val postsToAdd = Json.parse(Form("jsonPosts" -> text).bindFromRequest.get).as[List[Post]]
	
	    Logger.debug("Importing...")
        postsToAdd.map(post => {
          println(post)
          postService.insert(post)
        })
	    Logger.debug("...done")
	    Future.successful(Redirect(routes.Application.index(None, None, None)))
      } else {
        throw new AccessDeniedException("You are not an admin!!!!")
      }
    }
  }

  def editPost = SecuredAction.async { implicit request =>
    if (request.identity.isAdmin) {
	  DB.withSession { implicit session =>
	    val postId = UUID.fromString(Form("postId" -> text).bindFromRequest.get)
	    val post = postService.find(postId)
	    Future.successful(Ok(html.composepost.form(createPostForm.fill(post), request.identity)))
	  }
    } else {
      throw new AccessDeniedException("You are not an admin!!!!")
    }
  }

  def submit = SecuredAction.async { implicit request =>
    if (request.identity.isAdmin) {
	  createPostForm.bindFromRequest.fold(
	    formWithErrors => {
	      Future.successful(BadRequest(views.html.composepost.form(formWithErrors, request.identity)))
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
	              if (post.published) {
	                val titlePart = java.net.URLEncoder.encode(post.title.replaceAll("\\<.*?\\>", "").replaceAll("\\&.*?\\;", "").replaceAll(" ", "-"), "UTF-8")
	                val datePart = post.created.toString("yyyy-MM-dd")
	                val permalink = FeedConfig.baseUrl + "/" + datePart + "/" + titlePart;
	                yoService.sendYoToSubscribers(permalink)
	              }
	              Future.successful(Redirect(routes.Application.index(None, None, None)))
	            }
	          }
	        }
	        Future.successful(Redirect(routes.Application.index(None, None, None)))
	      }
	    }
	  )
    } else {
      throw new AccessDeniedException("You are not an admin!!!!")
    }
  }
}
