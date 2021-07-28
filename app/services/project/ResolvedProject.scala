package services.project

import cats.data.NonEmptySet
import services.access.{ Access, AccessKind, Accessors }
import services.task.{ Progress, ResolvedTask }
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

  def progress(resolvedProject: ResolvedProject): Progress = {
//    def descend(resolvedProject: ResolvedProject)
    ???
  }

  private def transitiveAccessorsOf(
      accessorsOf: ResolvedProject => Accessors,
      resolvedProject: ResolvedProject
  ): Accessors = {
    def readAccessorsOf(resolvedProject: ResolvedProject): Vector[Accessors] =
      Accessors.allowUsers(
        accessorsOf(resolvedProject),
        NonEmptySet.of(resolvedProject.ownerId)
      ) +: resolvedProject.projectReferenceTasks.flatMap(r => readAccessorsOf(r.project))

    Accessors.intersectAll(readAccessorsOf(resolvedProject))
  }

}
