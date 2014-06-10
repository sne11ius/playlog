import play.api._
import play.api.Play.current
import play.api.mvc._
import models.database.AdminIdentifiers
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.i18n.{Messages, Lang}
import play.api.mvc.Results._
import play.api.GlobalSettings
import play.api.mvc.{SimpleResult, RequestHeader}
import com.mohiva.play.silhouette.core.{Logger, SecuredSettings}
import utils.di.SilhouetteModule
import scala.concurrent.Future
import com.google.inject.Guice
import controllers.routes

object Global extends GlobalSettings with SecuredSettings with Logger {
  
  /**
   * The Guice dependencies injector.
   */
  val injector = Guice.createInjector(new SilhouetteModule)
  
   /**
   * Loads the controller classes with the Guice injector,
   * in order to be able to inject dependencies directly into the controller.
   *
   * @param controllerClass The controller class to instantiate.
   * @return The instance of the controller class.
   * @throws Exception if the controller couldn't be instantiated.
   */
  override def getControllerInstance[A](controllerClass: Class[A]) = injector.getInstance(controllerClass)
  
  /**
   * Called when a user is not authenticated.
   *
   * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
   *
   * @param request The request header.
   * @param lang The currently selected language.
   * @return The result to send to the client.
   */
  override def onNotAuthenticated(request: RequestHeader, lang: Lang): Option[Future[SimpleResult]] = {
    // Some(Future.successful(Redirect(routes.AuthenticationController.signIn)))
    //Some(controllers.AuthenticationController.signIn)
    Logger.error("Not authenticated D:")
    None
  }
  
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
          //Logger.info("Setup not yet done. Redirecting to setup.")
          //return Some(controllers.Application.setup)
        }
      }
	}
    super.onRouteRequest(request)
  }
}
