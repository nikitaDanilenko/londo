package controllers

import cats.effect.{ ContextShift, IO }
import db.DbContext
import doobie.implicits._
import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
@Singleton
class HomeController @Inject() (
    val controllerComponents: ControllerComponents,
    dbContext: DbContext
)(implicit
    executionContext: ExecutionContext
) extends BaseController {

  implicit val cs: ContextShift[IO] = ContextShiftProvider.fromExecutionContext

  /**
    * Create an Action to render an HTML page.
    *
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index: Action[AnyContent] =
    Action.async { implicit request: Request[AnyContent] =>
      import dbContext._
      val result = run(PublicSchema.UserDao.query).transact(transactor).unsafeToFuture()
      result.map(u => Ok(s"Ah, progress! $u"))
    }

}
