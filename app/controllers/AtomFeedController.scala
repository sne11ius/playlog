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

class AtomFeedController @Inject() (postService: PostService) extends Controller {
  
  def createAtomFeed = Action {
    val config = FeedConfig(
      title = "wasis.nu/mit/blog",
      subtitle = "Blog about technical stuff.",
	  authorName = "Cornelius Lilge",
	  feedId = "urn:uuid:14369a20-1023-11e4-9191-0800200c9a66",
	  baseUrl = "http://wasis.nu/mit/blog",
	  summaryLength = 200,
	  copyright = "(c) 2014 - wasis.nu"
    )
    Ok(views.html.atomFeed(config, postService.findAllPublished(None))).as("application/atom+xml")
  }
  
}
