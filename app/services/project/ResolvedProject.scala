package services.project

import cats.data.{ NonEmptyList, NonEmptySet }
import services.access.{ Access, AccessKind, Accessors }
import services.task.{ Progress, ResolvedTask, WeightedProgress }
import services.user.UserId
import cats.instances.vector._

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

  def progress(resolvedProject: ResolvedProject): Option[Progress] = {
    def descend(resolvedProject: ResolvedProject): Option[Progress] = {
      val weightedProgresses =
        resolvedProject.plainTasks.map(p => WeightedProgress(p.weight, p.progress)) ++
          resolvedProject.projectReferenceTasks
            .flatMap(r => descend(r.project).map(WeightedProgress(r.weight, _)))
      NonEmptyList
        .fromFoldable(weightedProgresses)
        .map(WeightedProgress.average)
    }
    descend(resolvedProject)
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
