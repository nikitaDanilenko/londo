package services.user

import play.api.Configuration

sealed abstract case class FrontendConfiguration(
    url: String,
    registrationPattern: String
)

object FrontendConfiguration {

  def apply(configuration: Configuration): FrontendConfiguration =
    new FrontendConfiguration(
      url = configuration.get[String]("application.frontend.url"),
      registrationPattern = configuration.get[String]("application.frontend.registrationPattern")
    ) {}

}
