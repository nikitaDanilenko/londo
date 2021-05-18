package services.project

import cats.syntax.contravariantSemigroupal._
import cats.syntax.traverse._
import db.keys
import db.keys.{ ProjectId, UserId }
import db.models.{ ProjectReadAccess, ProjectReadAccessEntry, ProjectWriteAccess, ProjectWriteAccessEntry }
import errors.ServerError
import services.project.AccessFromDB.instances._
import services.project.AccessToDB.instances._
import services.task.Task

case class Project(
    id: ProjectId,
    tasks: Vector[Task],
    name: String,
    description: Option[String],
    ownerId: UserId,
    parentProjectId: Option[ProjectId],
    flatIfSingleTask: Boolean,
    readAccessors: ProjectAccess[AccessKind.Read],
    writeAccessors: ProjectAccess[AccessKind.Write]
)

object Project {

  def toRow(project: Project): DbComponents =
    DbComponents(project)

  def fromRow(
      dbComponents: DbComponents
  ): ServerError.Valid[Project] =
    (
      dbComponents.plainTasks.traverse(Task.fromPlainTaskRow),
      dbComponents.projectReferenceTasks.traverse(Task.fromProjectReferenceRow)
    )
      .mapN { (plainTasks, projectReferenceTasks) =>
        Project(
          id = keys.ProjectId(dbComponents.project.id),
          tasks = (plainTasks ++ projectReferenceTasks).toVector,
          name = dbComponents.project.name,
          description = dbComponents.project.description,
          ownerId = keys.UserId(dbComponents.project.ownerId),
          parentProjectId = dbComponents.project.parentProjectId.map(ProjectId.apply),
          flatIfSingleTask = dbComponents.project.flatIfSingleTask,
          readAccessors = ProjectAccess.fromDb(dbComponents.readAccess),
          writeAccessors = ProjectAccess.fromDb(dbComponents.writeAccess)
        )
      }

  sealed trait DbComponents {
    def project: db.models.Project
    def plainTasks: Seq[db.models.PlainTask]
    def projectReferenceTasks: Seq[db.models.ProjectReferenceTask]
    def readAccess: ProjectAccess.DbComponents[ProjectReadAccess, ProjectReadAccessEntry]
    def writeAccess: ProjectAccess.DbComponents[ProjectWriteAccess, ProjectWriteAccessEntry]
  }

  object DbComponents {

    private case class DbComponentsImpl(
        override val project: db.models.Project,
        override val plainTasks: Seq[db.models.PlainTask],
        override val projectReferenceTasks: Seq[db.models.ProjectReferenceTask],
        override val readAccess: ProjectAccess.DbComponents[ProjectReadAccess, ProjectReadAccessEntry],
        override val writeAccess: ProjectAccess.DbComponents[ProjectWriteAccess, ProjectWriteAccessEntry]
    ) extends DbComponents

    def apply(project: Project): DbComponents = {
      val (plainTasks, projectReferenceTasks) = project.tasks
        .map(Task.toRow(project.id, _))
        .foldLeft((Vector.empty[db.models.PlainTask], Vector.empty[db.models.ProjectReferenceTask])) {
          case ((ps, prs), next) =>
            next.fold(
              p => (ps :+ p, prs),
              pr => (ps, prs :+ pr)
            )
        }
      DbComponentsImpl(
        project = db.models.Project(
          id = project.id.uuid,
          ownerId = project.ownerId.uuid,
          name = project.name,
          description = project.description,
          parentProjectId = project.parentProjectId.map(_.uuid),
          flatIfSingleTask = project.flatIfSingleTask
        ),
        plainTasks = plainTasks,
        projectReferenceTasks = projectReferenceTasks,
        readAccess = ProjectAccess.toDb(project.id, project.readAccessors),
        writeAccess = ProjectAccess.toDb(project.id, project.writeAccessors)
      )
    }

    def fromComponents(
        project: db.models.Project,
        plainTasks: Seq[db.models.PlainTask],
        projectReferenceTasks: Seq[db.models.ProjectReferenceTask],
        readAccessors: ProjectAccess[AccessKind.Read],
        writeAccessors: ProjectAccess[AccessKind.Write]
    ): DbComponents = {
      val projectId = ProjectId(project.id)
      DbComponentsImpl(
        project = project,
        plainTasks = plainTasks,
        projectReferenceTasks = projectReferenceTasks,
        readAccess = ProjectAccess.DbComponents(projectId, readAccessors),
        writeAccess = ProjectAccess.DbComponents(projectId, writeAccessors)
      )
    }

  }

}
