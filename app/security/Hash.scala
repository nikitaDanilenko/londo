package security

import services.user.PasswordParameters
import spire.math.Natural
import utils.string.StringUtil.syntax._

import java.security.MessageDigest
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Hash {

  private val hashAlgorithm: String          = "PBKDF2WithHmacSHA256"
  private val signatureHashAlgorithm: String = "SHA-384"
  private val keyLength: Int                 = 512

  val defaultIterations: Natural = Natural(120000)

  def fromPassword(password: String, salt: String, iterations: Natural): String = {
    SecretKeyFactory
      .getInstance(hashAlgorithm)
      .generateSecret(new PBEKeySpec(password.toCharArray, salt.getBytes(), iterations.intValue, keyLength))
      .getEncoded
      .asBase64String
  }

  def verify(password: String, passwordParameters: PasswordParameters): Boolean =
    fromPassword(password, passwordParameters.salt, passwordParameters.iterations) == passwordParameters.hash

  def messageDigest(string: String): String =
    MessageDigest
      .getInstance(signatureHashAlgorithm)
      .digest(string.getBytes)
      .asBase64String

}
