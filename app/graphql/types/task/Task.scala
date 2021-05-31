package graphql.types.task

import graphql.types.project.ProjectId
import io.circe.generic.JsonCodec
import sangria.macros.derive
import sangria.macros.derive.deriveObjectType
import sangria.schema.{ ObjectType, OutputType }
import spire.math.Natural
import utils.json.CirceUtil.instances._
import utils.graphql.SangriaUtil.instances._

object Task {

  @JsonCodec
  case class Plain(
      id: TaskId,
      name: String,
      taskKind: TaskKind,
      unit: Option[String],
      progress: Progress,
      weight: Natural
  )

  object Plain {

    def fromInternal(plain: services.task.Task.Plain): Plain =
      Plain(
        id = TaskId.fromInternal(plain.id),
        name = plain.name,
        taskKind = TaskKind.fromInternal(plain.taskKind),
        unit = plain.unit,
        progress = Progress.fromInternal(plain.progress),
        weight = plain.weight
      )

    implicit val plainObjectType: ObjectType[Unit, Plain] = deriveObjectType[Unit, Plain]()
  }

  @JsonCodec
  case class ProjectReference(
      id: TaskId,
      projectReference: ProjectId,
      weight: Natural
  )

  object ProjectReference {

    def fromInternal(projectReference: services.task.Task.ProjectReference): ProjectReference =
      ProjectReference(
        id = TaskId.fromInternal(projectReference.id),
        projectReference = ProjectId.fromInternal(projectReference.projectReference),
        weight = projectReference.weight
      )

    implicit val projectReferenceOutputType: OutputType[ProjectReference] =
      derive.deriveObjectType[Unit, ProjectReference]()

  }

}
