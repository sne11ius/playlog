import play.api._
import play.api.Play.current
import play.api.mvc._
import models.database.AdminIdentifiers
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

object Global extends GlobalSettings {
  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }
  
  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    //println("Executed before every request:" + request.toString)
    DB.withSession { implicit session =>
      if (AdminIdentifiers.findAll.isEmpty) {
        if (!request.path.startsWith("/assets") && !request.path.startsWith("/setup")) {
          //Logger.error("Cannot do this D:")
          //Logger.debug(request.path)
          //Logger.error(request.toString)
          Logger.info("Setup not yet done. Redirecting to setup.")
          return Some(controllers.Application.setup)
        }
      }
	}
    super.onRouteRequest(request)
  }
}
