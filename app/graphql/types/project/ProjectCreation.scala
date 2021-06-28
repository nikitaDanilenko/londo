package graphql.types.project

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.user.UserId
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.macros.derive.deriveInputObjectType
import sangria.schema.InputObjectType
import spire.math.Natural
import utils.json.CirceUtil.instances._
import utils.graphql.SangriaUtil.instances._
import io.circe.generic.JsonCodec

@JsonCodec
case class ProjectCreation(
    ownerId: UserId,
    name: String,
    description: Option[String],
    parentProject: Option[ProjectId],
    weight: Natural,
    flatIfSingleTask: Boolean,
    readAccessors: Accessors,
    writeAccessors: Accessors
)

object ProjectCreation {

  implicit val projectCreationToInternal: ToInternal[ProjectCreation, services.project.ProjectCreation] =
    projectCreation =>
      services.project.ProjectCreation(
        ownerId = projectCreation.ownerId.toInternal,
        name = projectCreation.name,
        description = projectCreation.description,
        parentProject = projectCreation.parentProject.map(_.toInternal),
        weight = projectCreation.weight,
        flatIfSingleTask = projectCreation.flatIfSingleTask,
        readAccessors = projectCreation.readAccessors.toInternal,
        writeAccessors = projectCreation.writeAccessors.toInternal
      )

  implicit val projectCreationInputObjectType: InputObjectType[ProjectCreation] =
    deriveInputObjectType[ProjectCreation]()

  implicit lazy val projectCreationFromInput: FromInput[ProjectCreation] = circeDecoderFromInput[ProjectCreation]

}
