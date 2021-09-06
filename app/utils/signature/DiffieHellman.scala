package utils.signature

import java.util.Base64

object DiffieHellman {

  def sharedNumber(modulus: BigInt, publicPower: BigInt, privateExponent: BigInt): BigInt =
    publicPower.modPow(exp = privateExponent, m = modulus)

  def sharedSecret(modulus: BigInt, publicPower: BigInt, privateExponent: BigInt): String =
    Base64.getEncoder
      .encodeToString(
        sharedNumber(
          modulus = modulus,
          publicPower = publicPower,
          privateExponent = privateExponent
        ).toByteArray
      )

}
