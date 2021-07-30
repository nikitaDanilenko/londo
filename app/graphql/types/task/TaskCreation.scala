package graphql.types.task

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import math.Positive
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.schema.InputObjectType
import utils.json.CirceUtil.instances._
import utils.graphql.SangriaUtil.instances._
import sangria.marshalling.circe.circeDecoderFromInput

object TaskCreation {

  @JsonCodec
  case class PlainCreation(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Positive
  )

  object PlainCreation {

    implicit val taskCreationPlainCreationToInternal: ToInternal[PlainCreation, services.task.TaskCreation.Plain] =
      plain =>
        services.task.TaskCreation.Plain(
          name = plain.name,
          taskKind = plain.taskKind.toInternal,
          unit = plain.unit,
          progress = plain.progress.toInternal,
          weight = plain.weight
        )

    implicit val taskCreationPlainCreationInputObjectType: InputObjectType[TaskCreation.PlainCreation] =
      deriveInputObjectType[TaskCreation.PlainCreation]()

    implicit lazy val taskCreationPlainCreationFromInput: FromInput[TaskCreation.PlainCreation] =
      circeDecoderFromInput[TaskCreation.PlainCreation]

  }

  @JsonCodec
  case class ProjectReferenceCreation(
      weight: Positive,
      projectReferenceId: ProjectId
  )

  object ProjectReferenceCreation {

    implicit val taskCreationProjectReferenceCreationToInternal
        : ToInternal[ProjectReferenceCreation, services.task.TaskCreation.ProjectReference] =
      projectReference =>
        services.task.TaskCreation.ProjectReference(
          weight = projectReference.weight,
          projectReferenceId = projectReference.projectReferenceId.toInternal
        )

    implicit val taskCreationProjectReferenceCreationInputObjectType: InputObjectType[ProjectReferenceCreation] =
      deriveInputObjectType[ProjectReferenceCreation]()

    implicit lazy val taskCreationProjectReferenceCreationFromInput: FromInput[ProjectReferenceCreation] =
      circeDecoderFromInput[ProjectReferenceCreation]

  }

}
