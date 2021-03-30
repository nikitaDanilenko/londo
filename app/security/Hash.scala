package security

import services.user.PasswordParameters
import spire.math.Natural

import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Hash {

  private val hashAlgorithm: String = "PBKDF2WithHmacSHA256"
  private val keyLength: Int = 512

  def fromPassword(password: String, salt: String, iterations: Natural): String = {
    SecretKeyFactory
      .getInstance(hashAlgorithm)
      .generateSecret(new PBEKeySpec(password.toCharArray, salt.getBytes(), iterations.intValue, keyLength))
      .getEncoded
      .map("%02x".format(_))
      .mkString
  }

  def verify(password: String, passwordParameters: PasswordParameters): Boolean =
    fromPassword(password, passwordParameters.salt, passwordParameters.iterations) == passwordParameters.hash

}
