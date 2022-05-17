package utils.graphql

import sangria.ast.{ StringValue, Value }
import sangria.schema.ScalarType
import sangria.validation.{ ValueCoercionViolation, Violation }

import java.util.UUID
import scala.util.Try

object SangriaUtil {

  trait Instances {

    implicit val uuidType: ScalarType[UUID] =
      createScalarType(
        name = "UUID",
        matcher = {
          case StringValue(s, _, _, _, _) => s
        },
        parse = parseUUID,
        parseString = parseUUID,
        UUIDCoercionViolation
      )

    implicit val unitType: ScalarType[Unit] =
      createScalarType(
        name = "Unit",
        matcher = {
          case StringValue(s, _, _, _, _) => s
        },
        parse = parseUnit,
        parseString = parseUnit,
        UnitCoercionViolation
      )

  }

  object instances extends Instances

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

  case object UUIDCoercionViolation extends ValueCoercionViolation("Not a valid UUID representation")

  case object UnitCoercionViolation extends ValueCoercionViolation("Not a valid unit representation")

  case object NaturalCoercionViolation extends ValueCoercionViolation("Not a natural number")
  case object PositiveCoercionViolation extends ValueCoercionViolation("Not a positive natural number")
}
