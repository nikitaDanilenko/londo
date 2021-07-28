package utils.graphql

import math.Positive
import sangria.ast.{ BigIntValue, StringValue }
import sangria.schema.ScalarType
import sangria.validation.{ ValueCoercionViolation, Violation }
import spire.math.Natural

import java.util.UUID
import scala.util.Try

object SangriaUtil {

  trait Instances {

    // TODO: Consider abstraction, if similar types occur again
    implicit val uuidType: ScalarType[UUID] = ScalarType[UUID](
      name = "UUID",
      coerceInput = {
        case StringValue(s, _, _, _, _) => parseUUID(s)
        case _                          => Left(UUIDCoercionViolation)
      },
      coerceOutput = (d, _) => d.toString,
      coerceUserInput = {
        case s: String => parseUUID(s)
        case _         => Left(UUIDCoercionViolation)
      }
    )

    implicit val unitType: ScalarType[Unit] =
      ScalarType[Unit](
        name = "Unit",
        coerceInput = {
          case StringValue(s, _, _, _, _) => parseUnit(s)
          case _                          => Left(UnitCoercionViolation)
        },
        coerceOutput = (d, _) => d.toString,
        coerceUserInput = {
          case s: String => parseUnit(s)
          case _         => Left(UnitCoercionViolation)
        }
      )

    implicit val naturalType: ScalarType[Natural] = ScalarType[Natural](
      name = "Natural",
      coerceInput = {
        case BigIntValue(bi, _, _) => parseNatural(bi)
        case _                     => Left(NaturalCoercionViolation)
      },
      coerceOutput = (bi, _) => bi.toString(),
      coerceUserInput = {
        case s: String => Try(BigInt(s)).toEither.left.map(_ => NaturalCoercionViolation).flatMap(parseNatural)
        case _         => Left(NaturalCoercionViolation)
      }
    )

    implicit val positiveType: ScalarType[Positive] = ScalarType[Positive](
      name = "Positive",
      coerceInput = {
        case BigIntValue(bi, _, _) => parsePositive(bi)
        case _                     => Left(PositiveCoercionViolation)
      },
      coerceOutput = (bi, _) => bi.toString,
      coerceUserInput = {
        case s: String => Try(BigInt(s)).toEither.left.map(_ => PositiveCoercionViolation).flatMap(parsePositive)
        case _         => Left(PositiveCoercionViolation)
      }
    )

  }

  object instances extends Instances

  private def parseUUID(string: String): Either[Violation, UUID] =
    Try(UUID.fromString(string)).toEither.left.map(_ => UUIDCoercionViolation)

  private def parseUnit(string: String): Either[Violation, Unit] = {
    val plain = string.replace(" ", "")
    if (plain == ().toString)
      Right(())
    else Left(UnitCoercionViolation)
  }

  private def parseNatural(bigInt: BigInt): Either[Violation, Natural] =
    Try(Natural(bigInt)).toEither.left.map(_ => NaturalCoercionViolation)

  private def parsePositive(bigInt: BigInt): Either[Violation, Positive] =
    parseNatural(bigInt).flatMap(Positive(_).left.map(_ => PositiveCoercionViolation))

  case object UUIDCoercionViolation extends ValueCoercionViolation("Not a valid UUID representation")

  case object UnitCoercionViolation extends ValueCoercionViolation("Not a valid unit representation")

  case object NaturalCoercionViolation extends ValueCoercionViolation("Not a natural number")
  case object PositiveCoercionViolation extends ValueCoercionViolation("Not a positive natural number")
}
