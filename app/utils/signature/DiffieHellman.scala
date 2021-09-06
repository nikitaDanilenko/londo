package utils.signature

import java.util.Base64

object DiffieHellman {

  def sharedNumber(modulus: BigInt, publicNumber: BigInt, privateExponent: BigInt): BigInt =
    publicNumber.modPow(exp = privateExponent, m = modulus)

  def sharedSecret(modulus: BigInt, publicNumber: BigInt, privateExponent: BigInt): String =
    Base64.getEncoder
      .encodeToString(
        sharedNumber(
          modulus = modulus,
          publicNumber = publicNumber,
          privateExponent = privateExponent
        ).toByteArray
      )

}
