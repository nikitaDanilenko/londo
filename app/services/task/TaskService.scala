package services.task

import cats.Applicative
import cats.data.{ EitherT, NonEmptyList, Validated }
import cats.effect.{ Async, ContextShift }
import db.Transactionally
import db.generated.daos.{ PlainTaskDAO, ProjectReferenceTaskDAO }
import db.keys.ProjectId
import doobie.ConnectionIO
import errors.ServerError
import services.project.{ ProgressUpdate, TaskUpdate }

import javax.inject.Inject

class TaskService @Inject() (
    plainTaskDAO: PlainTaskDAO,
    projectReferenceTaskDAO: ProjectReferenceTaskDAO,
    transactionally: Transactionally
) {

  def createTask[F[_]: Async: ContextShift](
      projectId: ProjectId,
      taskCreation: TaskCreation
  ): F[ServerError.Valid[Task]] =
    transactionally(createTaskC(projectId, taskCreation))

  def createTaskC(
      projectId: ProjectId,
      taskCreation: TaskCreation
  ): ConnectionIO[ServerError.Valid[Task]] =
    for {
      createdTask <- Async[ConnectionIO].liftIO(TaskCreation.create(taskCreation))
      writtenTask <- Task.toRow(projectId, createdTask) match {
        case Left(plainTaskRow) =>
          plainTaskDAO
            .insertC(plainTaskRow)
            .map(Task.fromPlainTaskRow)
        case Right(projectReferenceRow) =>
          projectReferenceTaskDAO
            .insertC(projectReferenceRow)
            .map(Task.fromProjectReferenceRow)
      }
    } yield writtenTask

  def fetch[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Valid[Task]] =
    transactionally(fetchC(taskKey))

  def fetchC(taskKey: TaskKey): ConnectionIO[ServerError.Valid[Task]] = {
    val taskId = TaskKey.toTaskId(taskKey)
    plainTaskDAO
      .findC(taskId)
      .flatMap {
        case Some(plainTask) => Applicative[ConnectionIO].pure(Task.fromPlainTaskRow(plainTask))
        case None =>
          projectReferenceTaskDAO
            .findC(taskId)
            .map(
              ServerError
                .fromOption(_, ServerError.Task.NotFound)
                .andThen(Task.fromProjectReferenceRow)
            )
      }
  }

  def removeTask[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Valid[Task]] =
    transactionally(removeTaskC(taskKey))

  def removeTaskC(taskKey: TaskKey): ConnectionIO[ServerError.Valid[Task]] =
    for {
      task <- fetchC(taskKey)
      _ <- plainTaskDAO.deleteC(TaskKey.toTaskId(taskKey))
    } yield task

  def updateTask[F[_]: Async: ContextShift](
      taskKey: TaskKey,
      taskUpdate: TaskUpdate
  ): F[ServerError.Valid[Task]] =
    transactionally(updateTaskC(taskKey, taskUpdate))

  def updateTaskC(taskKey: TaskKey, taskUpdate: TaskUpdate): ConnectionIO[ServerError.Valid[Task]] = {
    val transformer = for {
      task <- EitherT(fetchC(taskKey).map(_.toEither))
      updatedTask <- replaceTaskT(taskKey.projectId, TaskUpdate.applyToTask(task, taskUpdate))
    } yield updatedTask

    transformer.value.map(Validated.fromEither)
  }

  def updateTaskProgress[F[_]: Async: ContextShift](
      taskKey: TaskKey,
      progressUpdate: ProgressUpdate
  ): F[ServerError.Valid[Task]] =
    transactionally(updateTaskProgressC(taskKey, progressUpdate))

  def updateTaskProgressC(taskKey: TaskKey, progressUpdate: ProgressUpdate): ConnectionIO[ServerError.Valid[Task]] = {
    val transformer = for {
      task <- EitherT(fetchC(taskKey).map(_.toEither))
      updatedTask <- replaceTaskT(taskKey.projectId, ProgressUpdate.applyToTask(task, progressUpdate))
    } yield updatedTask

    transformer.value.map(Validated.fromEither)
  }

  private def replaceTaskT(projectId: ProjectId, task: Task): EitherT[ConnectionIO, NonEmptyList[ServerError], Task] = {
    def writeAction[Row](
        row: Row,
        replaceFunction: Row => ConnectionIO[Row],
        conversionFunction: Row => ServerError.Valid[Task]
    ): EitherT[ConnectionIO, NonEmptyList[ServerError], Task] =
      for {
        writtenRow <- EitherT.liftF(replaceFunction(row))
        writtenTask <- EitherT.fromEither[ConnectionIO](conversionFunction(writtenRow).toEither)
      } yield writtenTask
    Task
      .toRow(projectId, task)
      .fold(
        writeAction(_, plainTaskDAO.replaceC, Task.fromPlainTaskRow),
        writeAction(_, projectReferenceTaskDAO.replaceC, Task.fromProjectReferenceRow)
      )
  }

}
