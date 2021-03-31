package utils.signature

import java.security.Signature

object SignatureValidator {

  val signatureAlgorithm: String = "SHA256withDSA"

  def validate(string: String, publicKey: String): Boolean = {
    val signature = Signature.getInstance(signatureAlgorithm)
    signature.initVerify(RSAUtil.publicKeyFromBase64String(publicKey))
    signature.verify(string.getBytes)
  }

}
