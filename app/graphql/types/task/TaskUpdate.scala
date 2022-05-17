package graphql.types.task

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.project.ProjectId
import graphql.types.util.Positive
import io.circe.generic.JsonCodec
import sangria.macros.derive.deriveInputObjectType
import sangria.marshalling.FromInput
import sangria.marshalling.circe.circeDecoderFromInput
import sangria.schema.InputObjectType

object TaskUpdate {

  @JsonCodec
  case class PlainUpdate(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      weight: Positive,
      progressUpdate: ProgressUpdate
  )

  object PlainUpdate {

    implicit val taskUpdatePlainUpdateToInternal: ToInternal[PlainUpdate, services.task.TaskUpdate.Plain] = plain =>
      services.task.TaskUpdate.Plain(
        name = plain.name,
        taskKind = plain.taskKind.toInternal,
        unit = plain.unit,
        weight = plain.weight.toInternal,
        progressUpdate = plain.progressUpdate.toInternal
      )

    implicit val taskUpdatePlainUpdateInputObjectType: InputObjectType[PlainUpdate] =
      deriveInputObjectType[PlainUpdate]()

    implicit lazy val taskUpdatePlainUpdateFromInput: FromInput[PlainUpdate] = circeDecoderFromInput[PlainUpdate]

  }

  @JsonCodec
  case class ProjectReferenceUpdate(
      projectReferenceId: ProjectId,
      weight: Positive
  )

  object ProjectReferenceUpdate {

    implicit val taskUpdateProjectReferenceUpdateToInternal
        : ToInternal[ProjectReferenceUpdate, services.task.TaskUpdate.ProjectReference] =
      projectReference =>
        services.task.TaskUpdate.ProjectReference(
          projectReferenceId = projectReference.projectReferenceId.toInternal,
          weight = projectReference.weight.toInternal
        )

    implicit val taskUpdateProjectReferenceUpdateInputObjectType: InputObjectType[ProjectReferenceUpdate] =
      deriveInputObjectType[ProjectReferenceUpdate]()

    implicit lazy val taskUpdateProjectReferenceUpdateFromInput: FromInput[ProjectReferenceUpdate] =
      circeDecoderFromInput[ProjectReferenceUpdate]

  }

}
