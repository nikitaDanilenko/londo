package utils.signature

import utils.string.StringUtil.syntax._

import java.security.Signature
import scala.util.Try

object SignatureHandler {

  val signatureAlgorithm: String = "SHA512withRSA"

  def validate(signature: String, message: String, publicKey: String): Boolean = {
    val sig = Signature.getInstance(signatureAlgorithm)
    sig.initVerify(RSAUtil.publicKeyFromBase64String(publicKey))
    sig.update(message.getBytes)
    Try(sig.verify(signature.asBase64ByteArray)).getOrElse(false)
  }

  def sign(message: String, privateKey: String): String = {
    val signature = Signature.getInstance(signatureAlgorithm)
    signature.initSign(RSAUtil.privateKeyFromBase64String(privateKey))
    signature.update(message.getBytes)
    signature.sign().asBase64String
  }

}
