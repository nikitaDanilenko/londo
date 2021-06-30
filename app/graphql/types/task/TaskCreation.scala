package graphql.types.task

import graphql.types.ToInternal
import graphql.types.ToInternal.syntax._
import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import spire.math.Natural
import utils.json.CirceUtil.instances._
import utils.graphql.SangriaUtil.instances._

object TaskCreation {

  @JsonCodec
  case class Plain(
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Natural
  )

  object Plain {

    implicit val plainToInternal: ToInternal[Plain, services.task.TaskCreation.Plain] = plain =>
      services.task.TaskCreation.Plain(
        name = plain.name,
        taskKind = plain.taskKind.toInternal,
        unit = plain.unit,
        progress = plain.progress.toInternal,
        weight = plain.weight
      )

  }

  @JsonCodec
  case class ProjectReference(
      weight: Natural,
      projectReferenceId: ProjectId
  )

  object ProjectReference {

    implicit val projectReferenceToInternal: ToInternal[ProjectReference, services.task.TaskCreation.ProjectReference] =
      projectReference =>
        services.task.TaskCreation.ProjectReference(
          weight = projectReference.weight,
          projectReferenceId = projectReference.projectReferenceId.toInternal
        )

  }

}
