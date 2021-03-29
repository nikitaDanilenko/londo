package utils.signature

import java.security.spec.{ PKCS8EncodedKeySpec, X509EncodedKeySpec }
import java.security.{ KeyFactory, KeyPair }
import java.util.Base64

case class RSAKeyPair(
    privateKey: String,
    publicKey: String
)

object RSAKeyPair {

  def toJava(rsaKeyPair: RSAKeyPair): KeyPair = {
    val factory = KeyFactory.getInstance("RSA")
    val publicKey = factory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder.decode(rsaKeyPair.publicKey)))
    val privateKey = factory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder.decode(rsaKeyPair.privateKey)))
    new KeyPair(publicKey, privateKey)
  }

}
