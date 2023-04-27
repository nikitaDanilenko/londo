package graphql.queries.user.inputs

import graphql.types.user.UserId
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

case class FindUserResult(
    id: UserId,
    nickname: String
)

object FindUserResult {
  implicit val objectType: ObjectType[Unit, FindUserResult] = deriveObjectType[Unit, FindUserResult]()
}
