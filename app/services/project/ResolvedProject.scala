package services.project

import cats.data.{ NonEmptyList, NonEmptySet }
import cats.instances.vector._
import services.access.{ Access, AccessKind, Accessors }
import services.task.{ Progress, ResolvedTask, WeightedProgress }
import services.user.UserId
import spire.syntax.additiveSemigroup._
import cats.syntax.contravariantSemigroupal._

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

  // TODO: Check correctness of implementation - account for correct weight for referenced projects without progress
  def progress(resolvedProject: ResolvedProject): Option[Progress] = {
    def descend(resolvedProject: ResolvedProject): Option[Progress] = {
      val overallWeight = NonEmptyList
        .fromFoldable(resolvedProject.plainTasks.map(_.weight) ++ resolvedProject.projectReferenceTasks.map(_.weight))
        .map(ws => ws.tail.foldLeft(ws.head)(_ + _))

      val weightedProgresses =
        resolvedProject.plainTasks.map(p => WeightedProgress(p.weight, p.progress)) ++
          resolvedProject.projectReferenceTasks
            .flatMap(r => descend(r.project).map(WeightedProgress(r.weight, _)))
      (
        NonEmptyList.fromFoldable(weightedProgresses),
        overallWeight
      ).mapN(WeightedProgress.average)

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
