package utils.graphql

import sangria.ast.{ StringValue, Value }
import sangria.schema.ScalarType
import sangria.validation.{ BigDecimalCoercionViolation, ValueCoercionViolation, Violation }

import java.util.UUID
import scala.util.Try

object SangriaUtil {

  object instances {

    implicit val uuid: ScalarType[UUID] =
      createScalarType(
        name = "UUID",
        matcher = { case StringValue(s, _, _, _, _) =>
          s
        },
        parse = parseUUID,
        parseString = parseUUID,
        UUIDCoercionViolation
      )

    implicit val unit: ScalarType[Unit] =
      createScalarType(
        name = "Unit",
        matcher = { case StringValue(s, _, _, _, _) =>
          s
        },
        parse = parseUnit,
        parseString = parseUnit,
        UnitCoercionViolation
      )

    implicit val bigInt: ScalarType[BigInt] =
      createScalarType(
        name = "BigInt",
        matcher = { case StringValue(s, _, _, _, _) =>
          s
        },
        parse = parseBigInt,
        parseString = parseBigInt,
        BigIntCoercionViolation
      )

    implicit val bigDecimal: ScalarType[BigDecimal] =
      createScalarType(
        name = "BigDecimal",
        matcher = { case StringValue(s, _, _, _, _) =>
          s
        },
        parse = parseBigDecimal,
        parseString = parseBigDecimal,
        BigDecimalCoercionViolation
      )

  }

  private def createScalarType[A, Rep](
      name: String,
      matcher: PartialFunction[Value, Rep],
      parse: Rep => Either[Violation, A],
      parseString: String => Either[Violation, A],
      violation: => Violation
  ): ScalarType[A] =
    ScalarType[A](
      name = name,
      coerceInput = { v =>
        Try(matcher(v)).toEither
          .flatMap(parse)
          .left
          .map(_ => violation)
      },
      coerceOutput = (a, _) => a.toString,
      coerceUserInput = {
        case s: String => parseString(s)
        case _         => Left(violation)
      }
    )

  private def parseUUID(string: String): Either[Violation, UUID] =
    Try(UUID.fromString(string)).toEither.left.map(_ => UUIDCoercionViolation)

  private def parseUnit(string: String): Either[Violation, Unit] = {
    val plain = string.replace(" ", "")
    if (plain == ().toString)
      Right(())
    else Left(UnitCoercionViolation)
  }

  private def parseBigInt(string: String): Either[Violation, BigInt] =
    Try(BigInt(string)).toEither.left
      .map(_ => BigIntCoercionViolation)

  private def parseBigDecimal(string: String): Either[Violation, BigDecimal] =
    Try(BigDecimal(string)).toEither.left
      .map(_ => BigDecimalCoercionViolation)

  case object UUIDCoercionViolation extends ValueCoercionViolation("Not a valid UUID representation")

  case object UnitCoercionViolation extends ValueCoercionViolation("Not a valid unit representation")

  case object BigIntCoercionViolation extends ValueCoercionViolation("Not a valid BigInt representation")
//  case object BigDecimalCoercionViolation extends ValueCoercionViolation("Not a valid BigInt representation")

}
