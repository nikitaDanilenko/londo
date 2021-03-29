package utils.jwt

import play.api.Configuration

sealed trait JwtConfiguration {

  def signaturePublicKey: String
  def signaturePrivateKey: String

}

object JwtConfiguration {

  private case class JwtConfigurationImpl(
      override val signaturePublicKey: String,
      override val signaturePrivateKey: String
  ) extends JwtConfiguration

  def apply(configuration: Configuration): JwtConfiguration =
    JwtConfigurationImpl(
      signaturePublicKey = configuration.get[String]("application.jwt.signaturePublicKey"),
      signaturePrivateKey = configuration.get[String]("application.jwt.signaturePrivateKey")
    )

}
