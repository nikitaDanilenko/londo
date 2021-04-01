package utils.string

import java.util.Base64

object StringUtil {

  def toBase64String(bytes: Array[Byte]): String =
    Base64.getEncoder.encodeToString(bytes)

  def fromBase64String(string: String): Array[Byte] =
    Base64.getDecoder.decode(string)

  object syntax {

    implicit class ByteArrayToBase64String(val bytes: Array[Byte]) extends AnyVal {
      def asBase64String: String = toBase64String(bytes)
    }

    implicit class StringAsBase64ByteArray(val string: String) extends AnyVal {
      def asBase64ByteArray: Array[Byte] = fromBase64String(string)
    }

  }

}
