package graphql.queries.project

import graphql.types.project.Project
import graphql.types.task.Task
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class ResolvedProject(
    project: Project,
    tasks: Seq[Task]
)

object ResolvedProject {
  implicit lazy val outputType: OutputType[ResolvedProject] = deriveObjectType[Unit, ResolvedProject]()
}
