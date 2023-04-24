package controllers

import play.api.mvc.{ Request, WrappedRequest }
import security.jwt.LoggedIn

case class LoggedInRequest[A](
    request: Request[A],
    loggedIn: Option[LoggedIn]
) extends WrappedRequest[A](request)
