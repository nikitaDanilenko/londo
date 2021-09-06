package utils.signature

import java.util.Base64

object DiffieHellman {

  def sharedNumber(modulus: BigInt, publicExponent: BigInt, privateExponent: BigInt): BigInt =
    publicExponent.modPow(exp = privateExponent, m = modulus)

  def sharedSecret(modulus: BigInt, publicExponent: BigInt, privateExponent: BigInt): String =
    Base64.getEncoder
      .encodeToString(
        sharedNumber(
          modulus = modulus,
          publicExponent = publicExponent,
          privateExponent = privateExponent
        ).toByteArray
      )

}
