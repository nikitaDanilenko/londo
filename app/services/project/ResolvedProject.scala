package services.project

import services.access.{ Access, AccessKind, Accessors }
import services.task.ResolvedTask
import services.user.UserId

case class ResolvedProject(
    id: ProjectId,
    plainTasks: Vector[ResolvedTask.Plain],
    projectReferenceTasks: Vector[ResolvedTask.ProjectReference],
    name: String,
    description: Option[String],
    ownerId: UserId,
    flatIfSingleTask: Boolean,
    readAccessors: Access[AccessKind.Read],
    writeAccessors: Access[AccessKind.Write]
)

object ResolvedProject {

  def transitiveReadAccessOf(resolvedProject: ResolvedProject): Access[AccessKind.Read] =
    Access(transitiveAccessorsOf(_.readAccessors.accessors, resolvedProject))

  def transitiveWriteAccessOf(resolvedProject: ResolvedProject): Access[AccessKind.Write] =
    Access(transitiveAccessorsOf(_.writeAccessors.accessors, resolvedProject))

  private def transitiveAccessorsOf(
      accessorsOf: ResolvedProject => Accessors,
      resolvedProject: ResolvedProject
  ): Accessors = {
    def readAccessorsOf(resolvedProject: ResolvedProject): Vector[Accessors] =
      accessorsOf(resolvedProject) +: resolvedProject.projectReferenceTasks.flatMap(r => readAccessorsOf(r.project))

    Accessors.intersectAll(readAccessorsOf(resolvedProject))
  }

}
