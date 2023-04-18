package security.jwt

import play.api.Configuration

// TODO: Use pureconfig
sealed abstract case class JwtConfiguration(
    signaturePublicKey: String,
    signaturePrivateKey: String,
    restrictedDurationInSeconds: Long
)

object JwtConfiguration {

  def apply(configuration: Configuration): JwtConfiguration =
    new JwtConfiguration(
      signaturePublicKey = configuration.get[String]("application.jwt.signaturePublicKey"),
      signaturePrivateKey = configuration.get[String]("application.jwt.signaturePrivateKey"),
      restrictedDurationInSeconds = configuration.get[Long]("application.jwt.restrictedDurationInSeconds")
    ) {}

}
