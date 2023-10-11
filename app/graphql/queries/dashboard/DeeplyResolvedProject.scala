package graphql.queries.dashboard

import graphql.types.project.Project
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class DeeplyResolvedProject(
    project: Project,
    tasks: Seq[ResolvedTask]
)

object DeeplyResolvedProject {
  implicit lazy val outputType: OutputType[DeeplyResolvedProject] = deriveObjectType[Unit, DeeplyResolvedProject]()
}
