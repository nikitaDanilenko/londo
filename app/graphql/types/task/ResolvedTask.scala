package graphql.types.task

import graphql.types.FromInternal
import graphql.types.project.ResolvedProject
import io.circe.generic.JsonCodec
import sangria.macros.derive
import sangria.macros.derive.{ ObjectTypeName, deriveObjectType }
import sangria.schema.{ ObjectType, OutputType }
import utils.json.CirceUtil.instances._
import utils.graphql.SangriaUtil.instances._
import graphql.types.FromInternal.syntax._
import math.Positive

object ResolvedTask {

  @JsonCodec
  case class Plain(
      id: TaskId,
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Positive
  )

  object Plain {

    implicit val plainFromInternal: FromInternal[Plain, services.task.ResolvedTask.Plain] =
      plain =>
        Plain(
          id = plain.id.fromInternal,
          name = plain.name,
          taskKind = plain.taskKind.fromInternal,
          unit = plain.unit,
          progress = plain.progress.fromInternal,
          weight = plain.weight
        )

    implicit val plainObjectType: ObjectType[Unit, Plain] = deriveObjectType[Unit, Plain](
      ObjectTypeName("ResolvedPlain")
    )

  }

  @JsonCodec
  case class ProjectReference(
      id: TaskId,
      project: ResolvedProject,
      weight: Positive
  )

  object ProjectReference {

    implicit lazy val projectReferenceFromInternal
        : FromInternal[ProjectReference, services.task.ResolvedTask.ProjectReference] = projectReference =>
      ProjectReference(
        id = projectReference.id.fromInternal,
        project = projectReference.project.fromInternal,
        weight = projectReference.weight
      )

    implicit val projectReferenceOutputType: OutputType[ProjectReference] =
      derive.deriveObjectType[Unit, ProjectReference](
        ObjectTypeName("ResolvedProjectReference")
      )

  }

}
