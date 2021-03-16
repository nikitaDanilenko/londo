package graphql.types

import graphql.GraphQLContext
import sangria.macros.derive._
import sangria.schema.{ ObjectType, ScalarAlias, StringType }
import sangria.validation.Violation

import java.util.UUID
import scala.util.Try

object ModelTypes {

//  implicit val UserIdType: ObjectType[GraphQLContext, UserId] = deriveObjectType[GraphQLContext, UserId]()
//  implicit val UserType: ObjectType[GraphQLContext, User] = deriveObjectType[GraphQLContext, User]()
  implicit val uuidType: ScalarAlias[UUID, String] = ScalarAlias[UUID, String](
    StringType,
    _.toString,
    s =>
      Try(UUID.fromString(s)).toEither.left.map(e => new Violation { override def errorMessage: String = e.getMessage })
  )

  implicit val dbUserType: ObjectType[GraphQLContext, db.models.User] =
    deriveObjectType[GraphQLContext, db.models.User]()

}
