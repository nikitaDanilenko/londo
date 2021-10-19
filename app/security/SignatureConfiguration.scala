package security

import play.api.Configuration
import utils.signature.SignatureHandler

import java.security.{ PrivateKey, PublicKey }

sealed abstract case class SignatureConfiguration(
    privateKey: PrivateKey,
    publicKey: PublicKey
)

object SignatureConfiguration {

  def apply(configuration: Configuration): SignatureConfiguration =
    new SignatureConfiguration(
      privateKey = SignatureHandler
        .privateKeyFromPKCS8String(configuration.get[String]("application.signature.privateKey")),
      publicKey = SignatureHandler
        .publicKeyFromX509String(configuration.get[String]("application.signature.publicKey"))
    ) {}

}
