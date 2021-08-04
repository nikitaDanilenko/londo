package graphql.types.project

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.access.Accessors
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

@JsonCodec
case class ProjectCreation(
    name: String,
    description: Option[String],
    flatIfSingleTask: Boolean,
    readAccessors: Accessors,
    writeAccessors: Accessors
)

object ProjectCreation {

  implicit val projectCreationToInternal: ToInternal[ProjectCreation, services.project.ProjectCreation] =
    projectCreation =>
      services.project.ProjectCreation(
        name = projectCreation.name,
        description = projectCreation.description,
        flatIfSingleTask = projectCreation.flatIfSingleTask,
        readAccessors = projectCreation.readAccessors.toInternal,
        writeAccessors = projectCreation.writeAccessors.toInternal
      )

  implicit val projectCreationInputObjectType: InputObjectType[ProjectCreation] =
    deriveInputObjectType[ProjectCreation]()

  implicit lazy val projectCreationFromInput: FromInput[ProjectCreation] = circeDecoderFromInput[ProjectCreation]

}
