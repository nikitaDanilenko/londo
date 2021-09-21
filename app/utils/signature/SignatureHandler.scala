package utils.signature

import utils.string.StringUtil.syntax._

import java.security.spec.PKCS8EncodedKeySpec
import java.security.{ KeyFactory, PrivateKey, PublicKey, Signature }

object SignatureHandler {

  val signatureAlgorithm: String = "SHA3-512withRSA"
  val keyAlgorithm: String = "RSA"

  def sign(message: String, privateKey: PrivateKey): String = {
    val signature = Signature.getInstance(signatureAlgorithm)
    signature.initSign(privateKey)
    signature.update(message.getBytes)
    signature.sign().asBase64String
  }

  def privateKeyFromPKCS8String(string: String): PrivateKey = {
    val keyFactory = KeyFactory.getInstance(keyAlgorithm)
    keyFactory.generatePrivate(new PKCS8EncodedKeySpec(string.asBase64ByteArray))
  }

  def publicKeyFromPKCS8String(string: String): PublicKey = {
    val keyFactory = KeyFactory.getInstance(keyAlgorithm)
    keyFactory.generatePublic(new PKCS8EncodedKeySpec(string.asBase64ByteArray))
  }

}
