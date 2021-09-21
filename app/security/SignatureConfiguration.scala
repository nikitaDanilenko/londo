package security

import play.api.Configuration

sealed abstract case class SignatureConfiguration(
    modulus: BigInt,
    primitiveRoot: BigInt,
    backendExponent: BigInt
)

object SignatureConfiguration {

  def apply(configuration: Configuration): SignatureConfiguration =
    new SignatureConfiguration(
      modulus = BigInt(configuration.get[String]("application.signature.modulus")),
      primitiveRoot = BigInt(configuration.get[String]("application.signature.primitiveRoot")),
      backendExponent = BigInt(configuration.get[String]("application.signature.backendExponent"))
    ) {}

}
