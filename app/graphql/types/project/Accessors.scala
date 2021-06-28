package graphql.types.project

import graphql.types.FromInternal.syntax._
import graphql.types.ToInternal.syntax._
import graphql.types.user.UserId
import graphql.types.util.NonEmptyList
import graphql.types.FromAndToInternal
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._

@JsonCodec
case class Accessors(
    isAllowList: Boolean,
    //An empty option is taken to mean "everyone".
    userIds: Option[NonEmptyList[UserId]]
)

object Accessors {

  implicit val accessorsFromAndToInternal: FromAndToInternal[Accessors, services.project.Accessors.Representation] =
    FromAndToInternal.create(
      fromInternal = accessors =>
        Accessors(
          isAllowList = accessors.isAllowList,
          userIds = accessors.userIds.map(_.fromInternal)
        ),
      toInternal = accessors =>
        services.project.Accessors.Representation(
          isAllowList = accessors.isAllowList,
          userIds = accessors.userIds.map(_.toInternal)
        )
    )

  implicit lazy val accessorsFromInput: FromInput[Accessors] = circeDecoderFromInput[Accessors]

  implicit val accessorsInputObjectType: InputObjectType[Accessors] = deriveInputObjectType[Accessors](
    InputObjectTypeName("AccessorsInput")
  )

  implicit val accessorsObjectType: ObjectType[Unit, Accessors] = deriveObjectType[Unit, Accessors]()

}
