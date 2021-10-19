package services.user

import play.api.Configuration

sealed abstract case class FrontendConfiguration(
    url: String,
    emailPlaceholder: String,
    tokenPlaceholder: String,
    registrationPattern: String
)

object FrontendConfiguration {

  def apply(configuration: Configuration): FrontendConfiguration =
    new FrontendConfiguration(
      url = configuration.get[String]("application.frontend.url"),
      emailPlaceholder = configuration.get[String]("application.frontend.emailPlaceholder"),
      tokenPlaceholder = configuration.get[String]("application.frontend.tokenPlaceholder"),
      registrationPattern = configuration.get[String]("application.frontend.registrationPattern")
    ) {}

}
