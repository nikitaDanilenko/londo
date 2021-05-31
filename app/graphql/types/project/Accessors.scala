package graphql.types.project

import graphql.types.user.UserId
import graphql.types.util.NonEmptyList
import io.circe.generic.JsonCodec
import sangria.macros.derive.{ InputObjectTypeName, deriveInputObjectType, deriveObjectType }
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.{ InputObjectType, ObjectType }
import utils.graphql.SangriaUtil.instances._
import cats.syntax.functor._

@JsonCodec
case class Accessors(
    isAllowList: Boolean,
    //An empty option is taken to mean "everyone".
    userIds: Option[NonEmptyList[UserId]]
)

object Accessors {
  implicit lazy val accessorsFromInput: FromInput[Accessors] = circeDecoderFromInput[Accessors]

  implicit val accessorsInputObjectType: InputObjectType[Accessors] = deriveInputObjectType[Accessors](
    InputObjectTypeName("AccessorsInput")
  )

  implicit val accessorsObjectType: ObjectType[Unit, Accessors] = deriveObjectType[Unit, Accessors]()

  def fromInternal(accessors: services.project.Accessors.Representation): Accessors =
    Accessors(
      isAllowList = accessors.isAllowList,
      userIds = accessors.userIds.map(ids => NonEmptyList.fromInternal(ids.map(UserId.fromInternal)))
    )

  def toInternal(accessors: Accessors): services.project.Accessors.Representation =
    services.project.Accessors.Representation(
      isAllowList = accessors.isAllowList,
      userIds = accessors.userIds.map(ids => NonEmptyList.toInternal(ids.map(UserId.toInternal)))
    )

}
