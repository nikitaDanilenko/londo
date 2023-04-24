package graphql.queries.user

import graphql.types.user.UserId
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

case class FindUserResult(
    id: UserId,
    nickname: String
)

object FindUserResult {
  implicit val userIdObjectType: ObjectType[Unit, FindUserResult] = deriveObjectType[Unit, FindUserResult]()
}
