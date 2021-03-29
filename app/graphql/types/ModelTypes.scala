package graphql.types

import graphql.GraphQLContext
import sangria.ast.StringValue
import sangria.macros.derive._
import sangria.schema.{ ObjectType, ScalarType }
import sangria.validation.{ ValueCoercionViolation, Violation }
import services.user.{ User, UserDetails, UserId, UserSettings }

import java.util.UUID
import scala.util.Try

object ModelTypes {

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

  implicit val userIdType: ObjectType[GraphQLContext, UserId] =
    deriveObjectType[GraphQLContext, UserId]()

  implicit val userDetailsType: ObjectType[GraphQLContext, UserDetails] =
    deriveObjectType[GraphQLContext, UserDetails]()

  implicit val userSettingsType: ObjectType[GraphQLContext, UserSettings] =
    deriveObjectType[GraphQLContext, UserSettings]()

  implicit val userType: ObjectType[GraphQLContext, User] =
    deriveObjectType[GraphQLContext, User]()

  case object UUIDCoercionViolation extends ValueCoercionViolation("Not a valid UUID representation")

  case object UnitCoercionViolation extends ValueCoercionViolation("Not a valid unit representation")

  private def parseUUID(string: String): Either[Violation, UUID] =
    Try(UUID.fromString(string)).toEither.left.map(_ => UUIDCoercionViolation)

  private def parseUnit(string: String): Either[Violation, Unit] = {
    val plain = string.replace(" ", "")
    if (plain == ().toString)
      Right(())
    else Left(UnitCoercionViolation)
  }

}
