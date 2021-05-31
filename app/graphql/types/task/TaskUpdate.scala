package graphql.types.task

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType
import spire.math.Natural
import utils.json.CirceUtil.instances._
import utils.graphql.SangriaUtil.instances._

object TaskUpdate {

  @JsonCodec
  case class Plain(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      weight: Natural
  )

  object Plain {

    def toInternal(plain: Plain): services.task.TaskUpdate.Plain =
      services.task.TaskUpdate.Plain(
        name = plain.name,
        taskKind = TaskKind.toInternal(plain.taskKind),
        unit = plain.unit,
        weight = plain.weight
      )

    implicit val plainInputObjectType: InputObjectType[Plain] = deriveInputObjectType[Plain]()

    implicit lazy val plainFromInput: FromInput[Plain] = circeDecoderFromInput[Plain]

  }

  @JsonCodec
  case class ProjectReference(
      projectReferenceId: ProjectId,
      weight: Natural
  )

  object ProjectReference {

    def toInternal(projectReference: ProjectReference): services.task.TaskUpdate.ProjectReference =
      services.task.TaskUpdate.ProjectReference(
        projectReferenceId = ProjectId.toInternal(projectReference.projectReferenceId),
        weight = projectReference.weight
      )

    implicit val projectReferenceInputObjectType: InputObjectType[ProjectReference] =
      deriveInputObjectType[ProjectReference]()

    implicit lazy val projectReferenceFromInput: FromInput[ProjectReference] = circeDecoderFromInput[ProjectReference]

  }

}
