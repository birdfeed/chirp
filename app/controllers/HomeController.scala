package controllers

import javax.inject._

import org.birdfeed.chirp.actions.ActionWithValidApiKey
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController extends Controller {

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = ActionWithValidApiKey {
    Action {
      Ok(views.html.index("Your new application is ready."))
    }
  }


}
