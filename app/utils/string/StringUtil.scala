package utils.string

import java.util.Base64

object StringUtil {

  def encodeBase64(string: String): String =
    toBase64String(string.getBytes)

  def toBase64String(bytes: Array[Byte]): String =
    new String(Base64.getEncoder.encode(bytes))

  def fromBase64String(string: String): Array[Byte] =
    Base64.getDecoder.decode(string)

}
