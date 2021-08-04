package services.task

import cats.data.EitherT
import cats.effect.{ Async, ContextShift, IO }
import db.Transactionally
import db.generated.daos.{ PlainTaskDAO, ProjectReferenceTaskDAO }
import doobie.ConnectionIO
import errors.{ ErrorContext, ServerError }
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
      creation = plainCreation,
      fromRow = TaskService.plainTaskFromRow,
      toRow = Task.Plain.toRow
    )(
      projectId = projectId,
      toTask = TaskCreation.Plain.create,
      insert = plainTaskDAO
        .insertC(_)
        .map(_.left.map(_ => ErrorContext.Task.Plain.Create.asServerError))
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
      creation = projectReferenceCreation,
      fromRow = TaskService.projectReferenceTaskFromRow,
      toRow = Task.ProjectReference.toRow
    )(
      projectId = projectId,
      toTask = TaskCreation.ProjectReference.create,
      insert = projectReferenceTaskDAO
        .insertC(_)
        .map(_.left.map(_ => ErrorContext.Task.ProjectReference.Create.asServerError))
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
    removeTaskC(
      taskKey = taskKey,
      deleteC = plainTaskDAO
        .deleteC(_)
        .map(_.left.map(_ => ErrorContext.Task.Plain.Delete.asServerError)),
      fromRow = TaskService.plainTaskFromRow
    )

  def removeProjectReferenceTask[F[_]: Async: ContextShift](
      taskKey: TaskKey
  ): F[ServerError.Or[Task.ProjectReference]] =
    transactionally(removeProjectReferenceTaskC(taskKey))

  def removeProjectReferenceTaskC(taskKey: TaskKey): ConnectionIO[ServerError.Or[Task.ProjectReference]] =
    removeTaskC(
      taskKey = taskKey,
      deleteC = projectReferenceTaskDAO
        .deleteC(_)
        .map(_.left.map(_ => ErrorContext.Task.ProjectReference.Delete.asServerError)),
      fromRow = TaskService.projectReferenceTaskFromRow
    )

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
      fromRow = TaskService.plainTaskFromRow,
      toRow = Task.Plain.toRow
    )(
      projectId = projectId,
      task = plainTask,
      replace = plainTaskDAO
        .replaceC(_)
        .map(_.left.map(_ => ErrorContext.Task.Plain.Replace.asServerError))
    )

  private def replaceProjectReferenceT(
      projectId: ProjectId,
      projectReferenceTask: Task.ProjectReference
  ): EitherT[ConnectionIO, ServerError, Task.ProjectReference] =
    replaceTaskT(
      fromRow = TaskService.projectReferenceTaskFromRow,
      toRow = Task.ProjectReference.toRow
    )(
      projectId = projectId,
      task = projectReferenceTask,
      replace = projectReferenceTaskDAO
        .replaceC(_)
        .map(_.left.map(_ => ErrorContext.Task.ProjectReference.Replace.asServerError))
    )

  private def replaceTaskT[Task, Row](fromRow: Row => ServerError.Or[Task], toRow: (ProjectId, Task) => Row)(
      projectId: ProjectId,
      task: Task,
      replace: Row => ConnectionIO[ServerError.Or[Row]]
  ): EitherT[ConnectionIO, ServerError, Task] =
    for {
      writtenRow <- EitherT(replace(toRow(projectId, task)))
      writtenTask <- EitherT.fromEither[ConnectionIO](fromRow(writtenRow))
    } yield writtenTask

  private def createTaskC[Creation, Task, Row](
      creation: Creation,
      fromRow: Row => ServerError.Or[Task],
      toRow: (ProjectId, Task) => Row
  )(
      projectId: ProjectId,
      toTask: Creation => IO[Task],
      insert: Row => ConnectionIO[ServerError.Or[Row]]
  ): ConnectionIO[ServerError.Or[Task]] = {
    val transformer = for {
      createdTask <- ServerError.liftC(Async[ConnectionIO].liftIO(toTask(creation)))
      writtenTask <- EitherT(insert(toRow(projectId, createdTask)))
      taskFromRow <- EitherT.fromEither[ConnectionIO](fromRow(writtenTask))
    } yield taskFromRow

    transformer.value
  }

  private def fetchTaskC[Task, Row](
      taskKey: TaskKey,
      find: db.keys.TaskId => ConnectionIO[Option[Row]],
      fromRow: Row => ServerError.Or[Task]
  ): ConnectionIO[ServerError.Or[Task]] =
    find(TaskKey.toTaskId(taskKey))
      .map(
        ServerError
          .fromOption(_, ErrorContext.Task.NotFound.asServerError)
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
      deleteC: db.keys.TaskId => ConnectionIO[ServerError.Or[Row]],
      fromRow: Row => ServerError.Or[Task]
  ): ConnectionIO[ServerError.Or[Task]] =
    EitherT(deleteC(TaskKey.toTaskId(taskKey)))
      .subflatMap(fromRow)
      .value

}

object TaskService {

  private val plainTaskFromRow: db.models.PlainTask => ServerError.Or[Task.Plain] =
    Task.Plain.fromRow(_) |> ServerError.fromValidated[Task.Plain]

  private val projectReferenceTaskFromRow: db.models.ProjectReferenceTask => ServerError.Or[Task.ProjectReference] =
    Task.ProjectReference.fromRow(_) |> ServerError.fromValidated[Task.ProjectReference]

}
