package utils.signature

import cats.effect.IO
import utils.string.StringUtil

import java.security.spec.{ PKCS8EncodedKeySpec, X509EncodedKeySpec }
import java.security.{ KeyFactory, KeyPairGenerator, PrivateKey, PublicKey }
import java.util.Base64

object RSAUtil {

  val algorithm: String = "RSA"

  def generateRSAKeyPair: IO[RSAKeyPair] =
    IO {
      val keyPairGenerator = KeyPairGenerator.getInstance(algorithm)
      keyPairGenerator.initialize(512)
      val keyPair = keyPairGenerator.generateKeyPair()
      pprint.log(keyPair.getPrivate)
      pprint.log(keyPair.getPublic)
      RSAKeyPair(
        privateKey = StringUtil.toBase64String(keyPair.getPrivate.getEncoded),
        publicKey = StringUtil.toBase64String(keyPair.getPublic.getEncoded)
      )
    }

  def publicKeyFromBase64String(base64KeyString: String): PublicKey =
    KeyFactory
      .getInstance(algorithm)
      .generatePublic(new X509EncodedKeySpec(Base64.getDecoder.decode(base64KeyString)))

  def privateKeyFromBase64String(base64KeyString: String): PrivateKey =
    KeyFactory
      .getInstance(algorithm)
      .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder.decode(base64KeyString)))

}
