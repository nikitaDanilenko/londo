package graphql.types.user

import graphql.types.FromAndToInternal
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

  implicit val userDetailsFromAndToInternal: FromAndToInternal[UserDetails, services.user.UserDetails] =
    FromAndToInternal.create(
      fromInternal = userDetails =>
        UserDetails(
          firstName = userDetails.firstName,
          lastName = userDetails.lastName,
          description = userDetails.description
        ),
      toInternal = userDetails =>
        services.user.UserDetails(
          firstName = userDetails.firstName,
          lastName = userDetails.lastName,
          description = userDetails.description
        )
    )

  implicit val userDetailsObjectType: ObjectType[Unit, UserDetails] = deriveObjectType[Unit, UserDetails]()

}
