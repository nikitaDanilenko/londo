package utils.signature

import cats.effect.IO
import utils.string.StringUtil

import java.security.KeyPairGenerator

object CryptoUtil {

  def generateRSAKeyPair: IO[RSAKeyPair] =
    IO {
      val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
      keyPairGenerator.initialize(512)
      val keyPair = keyPairGenerator.generateKeyPair()
      pprint.log(keyPair.getPrivate)
      pprint.log(keyPair.getPublic)
      RSAKeyPair(
        privateKey = StringUtil.toBase64String(keyPair.getPrivate.getEncoded),
        publicKey = StringUtil.toBase64String(keyPair.getPublic.getEncoded)
      )
    }

}
