package utils.math

import errors.ServerError
import spire.math.Natural

import scala.util.Try

object NaturalUtil {

  def fromInt(int: Int): ServerError.Or[Natural] =
    fromWith(int)(Natural(_), ServerError.Conversion.IntToNatural)

  def fromBigInt(bigInt: BigInt): ServerError.Or[Natural] =
    fromWith(bigInt)(Natural(_), ServerError.Conversion.BigIntToNatural)

  private def fromWith[A](a: A)(f: A => Natural, error: ServerError): ServerError.Or[Natural] =
    Try(f(a)).toEither.left
      .map(_ => error)

}
