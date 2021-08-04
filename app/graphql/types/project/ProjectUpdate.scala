package graphql.types.project

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.user.UserId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class ProjectUpdate(
    name: String,
    description: Option[String],
    ownerId: UserId,
    flatIfSingleTask: Boolean
)

object ProjectUpdate {

  implicit val projectUpdateToInternal: ToInternal[ProjectUpdate, services.project.ProjectUpdate] =
    projectUpdate =>
      services.project.ProjectUpdate(
        name = projectUpdate.name,
        description = projectUpdate.description,
        ownerId = projectUpdate.ownerId.toInternal,
        flatIfSingleTask = projectUpdate.flatIfSingleTask
      )

  implicit val projectUpdateInputObjectType: InputObjectType[ProjectUpdate] = deriveInputObjectType[ProjectUpdate]()

  implicit lazy val projectUpdateFromInput: FromInput[ProjectUpdate] = circeDecoderFromInput[ProjectUpdate]

}
