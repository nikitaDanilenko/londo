package graphql.queries.statistics

import graphql.types.project.Project
import sangria.macros.derive.deriveObjectType
import sangria.schema.OutputType

case class ProjectAnalysis(
    project: Project,
    tasks: Seq[TaskAnalysis]
)

object ProjectAnalysis {
  implicit lazy val outputType: OutputType[ProjectAnalysis] = deriveObjectType[Unit, ProjectAnalysis]()
}
