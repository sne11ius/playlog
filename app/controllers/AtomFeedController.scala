package controllers

import views._
import models.FeedConfig
import service.PostService
import javax.inject.Inject
import play.api.mvc.Controller
import play.api.mvc.Action

class AtomFeedController @Inject() (postService: PostService) extends Controller {
  
  def createAtomFeed = Action {
    Ok(views.html.atomFeed(FeedConfig, postService.findAllPublished(None))).as("application/atom+xml")
  }
  
}
