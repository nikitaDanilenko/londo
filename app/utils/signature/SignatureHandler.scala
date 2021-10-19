package utils.signature

import utils.string.StringUtil.syntax._

import java.security.spec.{ PKCS8EncodedKeySpec, X509EncodedKeySpec }
import java.security.{ KeyFactory, PrivateKey, PublicKey, Signature }

object SignatureHandler {

  val signatureAlgorithm: String = "SHA512withECDSA"
  val keyAlgorithm: String = "EC"

  def sign(message: String, privateKey: PrivateKey): String = {
    val signature = Signature.getInstance(signatureAlgorithm)
    signature.initSign(privateKey)
    signature.update(message.getBytes)
    signature.sign().asBase64String
  }

  def validate(message: String, publicKey: PublicKey, signatureString: String): Boolean = {
    val signature = Signature.getInstance(signatureAlgorithm)
    signature.initVerify(publicKey)
    signature.update(message.getBytes)
    signature.verify(signatureString.asBase64ByteArray)
  }

  def privateKeyFromPKCS8String(string: String): PrivateKey = {
    val keyFactory = KeyFactory.getInstance(keyAlgorithm)
    keyFactory.generatePrivate(new PKCS8EncodedKeySpec(string.asBase64ByteArray))
  }

  def publicKeyFromX509String(string: String): PublicKey = {
    val keyFactory = KeyFactory.getInstance(keyAlgorithm)
    keyFactory.generatePublic(new X509EncodedKeySpec(string.asBase64ByteArray))
  }

}
