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
import models.User
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import com.googlecode.htmlcompressor.compressor.HtmlCompressor

object Global extends WithFilters(HTMLCompressorFilter()) with SecuredSettings with Logger {
  
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
  /*
  override def onNotAuthenticated(request: RequestHeader, lang: Lang): Option[Future[SimpleResult]] = {
    Logger.error("Not authenticated D:")
    None
  }
  */  
  override def onStart(app: Application) {
    Logger.info("Application has started")
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }
  
  override def onRouteRequest(request: RequestHeader): Option[Handler] = {
    super.onRouteRequest(request)
  }
}

/**
 * Defines a user-defined HTML compressor filter.
 */
object HTMLCompressorFilter {

  /**
   * Creates the HTML compressor filter.
   *
   * @return The HTML compressor filter.
   */
  def apply() = new HTMLCompressorFilter({
    val compressor = new HtmlCompressor()

    compressor.setRemoveComments(true);            //if false keeps HTML comments (default is true)
    compressor.setRemoveMultiSpaces(true);         //if false keeps multiple whitespace characters (default is true)
    compressor.setRemoveIntertagSpaces(true);      //removes iter-tag whitespace characters
    compressor.setRemoveQuotes(true);              //removes unnecessary tag attribute quotes
    compressor.setSimpleDoctype(true);             //simplify existing doctype
    compressor.setRemoveScriptAttributes(true);    //remove optional attributes from script tags
    compressor.setRemoveStyleAttributes(true);     //remove optional attributes from style tags
    compressor.setRemoveLinkAttributes(true);      //remove optional attributes from link tags
    compressor.setRemoveFormAttributes(true);      //remove optional attributes from form tags
    compressor.setRemoveInputAttributes(true);     //remove optional attributes from input tags
    compressor.setSimpleBooleanAttributes(true);   //remove values from boolean tag attributes
    compressor.setRemoveJavaScriptProtocol(true);  //remove "javascript:" from inline event handlers
    compressor.setRemoveHttpProtocol(false);        //replace "http://" with "//" inside tag attributes
    compressor.setRemoveHttpsProtocol(false);       //replace "https://" with "//" inside tag attributes
    compressor.setPreserveLineBreaks(false);        //preserves original line breaks
    compressor.setRemoveSurroundingSpaces("html,div,ul,ol,li,br,p,nav"); //remove spaces around provided tags
    compressor
  })
}
