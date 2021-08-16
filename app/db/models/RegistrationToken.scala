package db.models

import services.user.FrontendConfiguration

case class RegistrationToken(email: String, token: String)

object RegistrationToken {

  def createRegistrationLink(
      frontendConfiguration: FrontendConfiguration,
      registrationToken: RegistrationToken
  ): String = {
    pprint.log(frontendConfiguration)
    val replaced = frontendConfiguration.registrationPattern
      .replace(frontendConfiguration.emailPlaceholder, registrationToken.email)
      .replace(frontendConfiguration.tokenPlaceholder, registrationToken.token)

    s"${frontendConfiguration.url}$replaced"
  }

}
