package graphql.types.access

import graphql.types.FromAndToInternal
import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.user.UserId
import graphql.types.util.NonEmptyList
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import services.access.Access
import utils.graphql.SangriaUtil.instances._

@JsonCodec
case class Accessors(
    isAllowList: Boolean,
    //An empty option is taken to mean "everyone".
    userIds: Option[NonEmptyList[UserId]]
)

object Accessors {

  implicit val accessorsFromAndToInternal: FromAndToInternal[Accessors, services.access.Accessors.Representation] =
    FromAndToInternal.create(
      fromInternal = accessors =>
        Accessors(
          isAllowList = accessors.isAllowList,
          userIds = accessors.userIds.map(_.fromInternal)
        ),
      toInternal = accessors =>
        services.access.Accessors.Representation(
          isAllowList = accessors.isAllowList,
          userIds = accessors.userIds.map(_.toInternal)
        )
    )

  implicit lazy val accessorsFromInput: FromInput[Accessors] = circeDecoderFromInput[Accessors]

  implicit val accessorsInputObjectType: InputObjectType[Accessors] = deriveInputObjectType[Accessors](
    InputObjectTypeName("AccessorsInput")
  )

  implicit val accessorsObjectType: ObjectType[Unit, Accessors] = deriveObjectType[Unit, Accessors]()

  def fromInternalAccess[AK](projectAccess: Access[AK]): Accessors =
    services.access.Accessors.toRepresentation(projectAccess.accessors).fromInternal

}
