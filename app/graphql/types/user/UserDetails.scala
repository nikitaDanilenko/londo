package graphql.types.user

import graphql.GraphQLContext
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

@JsonCodec
case class UserDetails(
    firstName: Option[String],
    lastName: Option[String],
    description: Option[String]
)

object UserDetails {

  implicit val userDetailsType: ObjectType[GraphQLContext, UserDetails] =
    deriveObjectType[GraphQLContext, UserDetails]()

  def fromInternal(userDetails: services.user.UserDetails): UserDetails =
    UserDetails(
      firstName = userDetails.firstName,
      lastName = userDetails.lastName,
      description = userDetails.description
    )

  def toInternal(userDetails: UserDetails): services.user.UserDetails =
    services.user.UserDetails(
      firstName = userDetails.firstName,
      lastName = userDetails.lastName,
      description = userDetails.description
    )

}
