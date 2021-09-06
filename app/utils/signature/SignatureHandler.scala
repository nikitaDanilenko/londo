package utils.signature

import utils.string.StringUtil.syntax._

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SignatureHandler {

  val signatureAlgorithm: String = "HmacSHA512"

  def validate(signature: String, message: String, secret: String): Boolean =
    signature == sign(message, secret)

  def sign(message: String, secret: String): String = {
    val mac = Mac.getInstance(signatureAlgorithm)
    mac.init(new SecretKeySpec(secret.getBytes, signatureAlgorithm))
    mac.update(secret.getBytes)
    mac.doFinal(message.getBytes).asBase64String
  }

}
