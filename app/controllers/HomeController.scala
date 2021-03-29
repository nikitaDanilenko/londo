package controllers

import play.api.mvc._

import javax.inject._

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (
    override protected val controllerComponents: ControllerComponents
) extends BaseController {

  def index: Action[AnyContent] =
    Action { _ =>
      Ok(s"Ah, progress!")
    }

}
