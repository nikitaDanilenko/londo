package utils.math

import cats.data.NonEmptyList
import errors.ServerError
import spire.math.Natural

import scala.util.Try

object NaturalUtil {

  def fromInt(int: Int): ServerError.Valid[Natural] =
    ServerError.fromEitherNel(
      Try(
        Natural(int)
      ).toEither.left.map(_ => NonEmptyList.of(ServerError.Conversion.IntToNatural))
    )

}
