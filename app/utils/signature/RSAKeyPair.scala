package utils.signature

import java.security.KeyPair

case class RSAKeyPair(
    privateKey: String,
    publicKey: String
)

object RSAKeyPair {

  def toJava(rsaKeyPair: RSAKeyPair): KeyPair = {
    new KeyPair(
      RSAUtil.publicKeyFromBase64String(rsaKeyPair.publicKey),
      RSAUtil.privateKeyFromBase64String(rsaKeyPair.privateKey)
    )
  }

}
