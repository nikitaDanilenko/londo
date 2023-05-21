package graphql.mutations.user.inputs

import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType
import services.user.User

@JsonCodec
case class UserIdentifier(
    nickname: String,
    email: String
)

object UserIdentifier {

  def of(user: User): UserIdentifier =
    UserIdentifier(
      nickname = user.nickname,
      email = user.email
    )

  implicit val inputObjectType: InputObjectType[UserIdentifier] =
    deriveInputObjectType[UserIdentifier]()

}
