package services.task

import cats.data.EitherT
import cats.effect.{ Async, ContextShift, IO }
import db.Transactionally
import db.generated.daos.{ PlainTaskDAO, ProjectReferenceTaskDAO }
import doobie.ConnectionIO
import errors.ServerError
import services.project.ProjectId
import utils.fp.FunctionUtil.syntax._

import javax.inject.Inject

class TaskService @Inject() (
    plainTaskDAO: PlainTaskDAO,
    projectReferenceTaskDAO: ProjectReferenceTaskDAO,
    transactionally: Transactionally
) {

  def createPlainTask[F[_]: Async: ContextShift](
      projectId: ProjectId,
      plainCreation: TaskCreation.Plain
  ): F[ServerError.Or[Task.Plain]] =
    transactionally(createPlainTaskC(projectId, plainCreation))

  def createPlainTaskC(
      projectId: ProjectId,
      plainCreation: TaskCreation.Plain
  ): ConnectionIO[ServerError.Or[Task.Plain]] =
    createTaskC(
      projectId = projectId,
      creation = plainCreation,
      toTask = TaskCreation.Plain.create,
      fromRow = TaskService.plainTaskFromRow,
      toRow = Task.Plain.toRow,
      insert = plainTaskDAO.insertC
    )

  def createProjectReferenceTask[F[_]: Async: ContextShift](
      projectId: ProjectId,
      projectReferenceCreation: TaskCreation.ProjectReference
  ): F[ServerError.Or[Task.ProjectReference]] =
    transactionally(createProjectReferenceTaskC(projectId, projectReferenceCreation))

  def createProjectReferenceTaskC(
      projectId: ProjectId,
      projectReferenceCreation: TaskCreation.ProjectReference
  ): ConnectionIO[ServerError.Or[Task.ProjectReference]] =
    createTaskC(
      projectId = projectId,
      creation = projectReferenceCreation,
      toTask = TaskCreation.ProjectReference.create,
      fromRow = TaskService.projectReferenceTaskFromRow,
      toRow = Task.ProjectReference.toRow,
      insert = projectReferenceTaskDAO.insertC
    )

  def fetchPlain[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Or[Task.Plain]] =
    transactionally(fetchPlainC(taskKey))

  def fetchPlainC(taskKey: TaskKey): ConnectionIO[ServerError.Or[Task.Plain]] =
    fetchTaskC(taskKey, plainTaskDAO.findC, TaskService.plainTaskFromRow)

  def fetchProjectReference[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Or[Task.ProjectReference]] =
    transactionally(fetchProjectReferenceC(taskKey))

  def fetchProjectReferenceC(taskKey: TaskKey): ConnectionIO[ServerError.Or[Task.ProjectReference]] =
    fetchTaskC(taskKey, projectReferenceTaskDAO.findC, TaskService.projectReferenceTaskFromRow)

  def removePlainTask[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Or[Task.Plain]] =
    transactionally(removePlainTaskC(taskKey))

  def removePlainTaskC(taskKey: TaskKey): ConnectionIO[ServerError.Or[Task.Plain]] =
    removeTaskC(taskKey, plainTaskDAO.deleteC, TaskService.plainTaskFromRow)

  def removeProjectReferenceTask[F[_]: Async: ContextShift](
      taskKey: TaskKey
  ): F[ServerError.Or[Task.ProjectReference]] =
    transactionally(removeProjectReferenceTaskC(taskKey))

  def removeProjectReferenceTaskC(taskKey: TaskKey): ConnectionIO[ServerError.Or[Task.ProjectReference]] =
    removeTaskC(taskKey, projectReferenceTaskDAO.deleteC, TaskService.projectReferenceTaskFromRow)

  def updatePlainTask[F[_]: Async: ContextShift](
      taskKey: TaskKey,
      plainUpdate: TaskUpdate.Plain
  ): F[ServerError.Or[Task.Plain]] =
    transactionally(updatePlainTaskC(taskKey, plainUpdate))

  def updatePlainTaskC(taskKey: TaskKey, plainUpdate: TaskUpdate.Plain): ConnectionIO[ServerError.Or[Task.Plain]] =
    updateTaskC(
      taskKey = taskKey,
      taskUpdate = plainUpdate,
      fetchC = fetchPlainC,
      applyUpdate = TaskUpdate.Plain.applyToTask,
      replaceTask = replacePlainT
    )

  def updateProjectReferenceTask[F[_]: Async: ContextShift](
      taskKey: TaskKey,
      plainUpdate: TaskUpdate.ProjectReference
  ): F[ServerError.Or[Task.ProjectReference]] =
    transactionally(updateProjectReferenceTaskC(taskKey, plainUpdate))

  def updateProjectReferenceTaskC(
      taskKey: TaskKey,
      plainUpdate: TaskUpdate.ProjectReference
  ): ConnectionIO[ServerError.Or[Task.ProjectReference]] =
    updateTaskC(
      taskKey = taskKey,
      taskUpdate = plainUpdate,
      fetchC = fetchProjectReferenceC,
      applyUpdate = TaskUpdate.ProjectReference.applyToTask,
      replaceTask = replaceProjectReferenceT
    )

  def updateTaskProgress[F[_]: Async: ContextShift](
      taskKey: TaskKey,
      progressUpdate: ProgressUpdate
  ): F[ServerError.Or[Task.Plain]] =
    transactionally(updateTaskProgressC(taskKey, progressUpdate))

  def updateTaskProgressC(
      taskKey: TaskKey,
      progressUpdate: ProgressUpdate
  ): ConnectionIO[ServerError.Or[Task.Plain]] = {
    val transformer = for {
      task <- EitherT(fetchTaskC(taskKey, plainTaskDAO.findC, TaskService.plainTaskFromRow))
      updatedTask <- replacePlainT(
        projectId = taskKey.projectId,
        plainTask = ProgressUpdate.applyToTask(task, progressUpdate)
      )
    } yield updatedTask

    transformer.value
  }

  private def replacePlainT(
      projectId: ProjectId,
      plainTask: Task.Plain
  ): EitherT[ConnectionIO, ServerError, Task.Plain] =
    replaceTaskT(
      projectId = projectId,
      task = plainTask,
      replace = plainTaskDAO.replaceC,
      fromRow = TaskService.plainTaskFromRow,
      toRow = Task.Plain.toRow
    )

  private def replaceProjectReferenceT(
      projectId: ProjectId,
      projectReferenceTask: Task.ProjectReference
  ): EitherT[ConnectionIO, ServerError, Task.ProjectReference] =
    replaceTaskT(
      projectId = projectId,
      task = projectReferenceTask,
      replace = projectReferenceTaskDAO.replaceC,
      fromRow = TaskService.projectReferenceTaskFromRow,
      toRow = Task.ProjectReference.toRow
    )

  private def replaceTaskT[Task, Row](
      projectId: ProjectId,
      task: Task,
      replace: Row => ConnectionIO[Row],
      fromRow: Row => ServerError.Or[Task],
      toRow: (ProjectId, Task) => Row
  ): EitherT[ConnectionIO, ServerError, Task] =
    for {
      writtenRow <- EitherT.liftF(replace(toRow(projectId, task)))
      writtenTask <- EitherT.fromEither[ConnectionIO](fromRow(writtenRow))
    } yield writtenTask

  private def createTaskC[Creation, Task, Row](
      projectId: ProjectId,
      creation: Creation,
      toTask: Creation => IO[Task],
      fromRow: Row => ServerError.Or[Task],
      toRow: (ProjectId, Task) => Row,
      insert: Row => ConnectionIO[Row]
  ): ConnectionIO[ServerError.Or[Task]] =
    for {
      createdTask <- Async[ConnectionIO].liftIO(toTask(creation))
      writtenTask <- insert(toRow(projectId, createdTask))
    } yield fromRow(writtenTask)

  private def fetchTaskC[Task, Row](
      taskKey: TaskKey,
      find: db.keys.TaskId => ConnectionIO[Option[Row]],
      fromRow: Row => ServerError.Or[Task]
  ): ConnectionIO[ServerError.Or[Task]] =
    find(TaskKey.toTaskId(taskKey))
      .map(
        ServerError
          .fromOption(_, ServerError.Task.NotFound)
          .flatMap(fromRow)
      )

  private def updateTaskC[Task, Update, Row](
      taskKey: TaskKey,
      taskUpdate: Update,
      fetchC: TaskKey => ConnectionIO[ServerError.Or[Task]],
      applyUpdate: (Task, Update) => Task,
      replaceTask: (ProjectId, Task) => EitherT[ConnectionIO, ServerError, Task]
  ): ConnectionIO[ServerError.Or[Task]] = {
    val transformer = for {
      task <- EitherT(fetchC(taskKey))
      updatedTask <- replaceTask(taskKey.projectId, applyUpdate(task, taskUpdate))
    } yield updatedTask

    transformer.value
  }

  private def removeTaskC[Row, Task](
      taskKey: TaskKey,
      deleteC: db.keys.TaskId => ConnectionIO[Row],
      fromRow: Row => ServerError.Or[Task]
  ): ConnectionIO[ServerError.Or[Task]] =
    deleteC(TaskKey.toTaskId(taskKey))
      .map(fromRow)

}

object TaskService {

  private val plainTaskFromRow: db.models.PlainTask => ServerError.Or[Task.Plain] =
    Task.Plain.fromRow(_) |> ServerError.fromValidated[Task.Plain]

  private val projectReferenceTaskFromRow: db.models.ProjectReferenceTask => ServerError.Or[Task.ProjectReference] =
    Task.ProjectReference.fromRow(_) |> ServerError.fromValidated[Task.ProjectReference]

}
