package security.jwt

import play.api.Configuration

sealed trait JwtConfiguration {

  def signaturePublicKey: String
  def signaturePrivateKey: String

  def restrictedDurationInSeconds: Long
}

object JwtConfiguration {

  private case class JwtConfigurationImpl(
      override val signaturePublicKey: String,
      override val signaturePrivateKey: String,
      override val restrictedDurationInSeconds: Long
  ) extends JwtConfiguration

  def apply(configuration: Configuration): JwtConfiguration =
    JwtConfigurationImpl(
      signaturePublicKey = configuration.get[String]("application.jwt.signaturePublicKey"),
      signaturePrivateKey = configuration.get[String]("application.jwt.signaturePrivateKey"),
      restrictedDurationInSeconds = configuration.get[Long]("application.jwt.restrictedDurationInSeconds")
    )

}
