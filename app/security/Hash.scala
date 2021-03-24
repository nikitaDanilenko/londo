package security

import java.security.MessageDigest

object Hash {

  private val hashAlgorithm: String = "SHA3-512"

  def fromPassword(password: String, salt: String): String =
    MessageDigest
      .getInstance(hashAlgorithm)
      .digest(List(password, salt).mkString.getBytes)
      .map("%02x".format(_))
      .mkString

  def verify(password: String, salt: String, hash: String): Boolean =
    fromPassword(password, salt) == hash

}
