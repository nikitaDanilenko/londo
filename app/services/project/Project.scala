package services.project

import cats.syntax.traverse._
import db.models.{ ProjectReadAccess, ProjectReadAccessEntry, ProjectWriteAccess, ProjectWriteAccessEntry }
import errors.ServerError
import services.project.AccessFromDB.instances._
import services.project.AccessToDB.instances._
import services.user.UserId

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
  ): ServerError.Valid[Project] = {

    dbComponents.tasks.traverse(Task.fromRow).map { tasks =>
      Project(
        id = ProjectId(dbComponents.project.id),
        tasks = tasks.toVector,
        name = dbComponents.project.name,
        description = dbComponents.project.description,
        ownerId = UserId(dbComponents.project.ownerId),
        parentProjectId = dbComponents.project.parentProjectId.map(ProjectId.apply),
        flatIfSingleTask = dbComponents.project.flatIfSingleTask,
        readAccessors = ProjectAccess.fromDb(dbComponents.readAccess),
        writeAccessors = ProjectAccess.fromDb(dbComponents.writeAccess)
      )
    }
  }

  sealed trait DbComponents {
    def project: db.models.Project
    def tasks: Seq[db.models.Task]
    def readAccess: Option[ProjectAccess.DbComponents[ProjectReadAccess, ProjectReadAccessEntry]]
    def writeAccess: Option[ProjectAccess.DbComponents[ProjectWriteAccess, ProjectWriteAccessEntry]]
  }

  object DbComponents {

    private case class DbComponentsImpl(
        override val project: db.models.Project,
        override val tasks: Seq[db.models.Task],
        override val readAccess: Option[ProjectAccess.DbComponents[ProjectReadAccess, ProjectReadAccessEntry]],
        override val writeAccess: Option[ProjectAccess.DbComponents[ProjectWriteAccess, ProjectWriteAccessEntry]]
    ) extends DbComponents

    def apply(project: Project): DbComponents =
      DbComponentsImpl(
        project = db.models.Project(
          id = project.id.uuid,
          ownerId = project.ownerId.uuid,
          name = project.name,
          description = project.description,
          parentProjectId = project.parentProjectId.map(_.uuid),
          flatIfSingleTask = project.flatIfSingleTask
        ),
        tasks = project.tasks.map(Task.toRow),
        readAccess = ProjectAccess.toDb(project.id, project.readAccessors),
        writeAccess = ProjectAccess.toDb(project.id, project.writeAccessors)
      )

  }

}
