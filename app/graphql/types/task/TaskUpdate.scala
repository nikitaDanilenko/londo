package graphql.types.task

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
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

    implicit val plainToInternal: ToInternal[Plain, services.task.TaskUpdate.Plain] = plain =>
      services.task.TaskUpdate.Plain(
        name = plain.name,
        taskKind = plain.taskKind.toInternal,
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

    implicit val projectReferenceToInternal: ToInternal[ProjectReference, services.task.TaskUpdate.ProjectReference] =
      projectReference =>
        services.task.TaskUpdate.ProjectReference(
          projectReferenceId = projectReference.projectReferenceId.toInternal,
          weight = projectReference.weight
        )

    implicit val projectReferenceInputObjectType: InputObjectType[ProjectReference] =
      deriveInputObjectType[ProjectReference]()

    implicit lazy val projectReferenceFromInput: FromInput[ProjectReference] = circeDecoderFromInput[ProjectReference]

  }

}
