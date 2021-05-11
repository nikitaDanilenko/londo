package services.task

import cats.data.{ EitherT, Validated }
import cats.effect.{ Async, ContextShift }
import db.Transactionally
import db.generated.daos.TaskDAO
import db.keys.ProjectId
import doobie.ConnectionIO
import errors.ServerError
import services.project.TaskUpdate
import spire.math.Natural

import javax.inject.Inject

class TaskService @Inject() (taskDAO: TaskDAO, transactionally: Transactionally) {

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
      taskRow = Task.toRow(projectId, createdTask)
      writtenTask <- taskDAO.insertC(taskRow)
    } yield Task.fromRow(writtenTask)

  def fetch[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Valid[Task]] =
    transactionally(fetchC(taskKey))

  def fetchC(taskKey: TaskKey): ConnectionIO[ServerError.Valid[Task]] =
    taskDAO
      .findC(TaskKey.toTaskId(taskKey))
      .map(
        ServerError
          .fromOption(_, ServerError.Task.NotFound)
          .andThen(Task.fromRow)
      )

  def removeTask[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Valid[Task]] =
    transactionally(removeTaskC(taskKey))

  def removeTaskC(taskKey: TaskKey): ConnectionIO[ServerError.Valid[Task]] =
    for {
      task <- fetchC(taskKey)
      _ <- taskDAO.deleteC(TaskKey.toTaskId(taskKey))
    } yield task

  def updateTask[F[_]: Async: ContextShift](
      taskKey: TaskKey,
      taskUpdate: TaskUpdate
  ): F[ServerError.Valid[Task]] =
    transactionally(updateTaskC(taskKey, taskUpdate))

  def updateTaskC(taskKey: TaskKey, taskUpdate: TaskUpdate): ConnectionIO[ServerError.Valid[Task]] = {
    val transformer = for {
      task <- EitherT(fetchC(taskKey).map(_.toEither))
      updatedTask = TaskUpdate.applyToTask(task, taskUpdate)
      updatedTaskRow <- EitherT.liftF(taskDAO.replaceC(Task.toRow(taskKey.projectId, updatedTask)))
      writtenUpdatedTask <- EitherT.fromEither[ConnectionIO](Task.fromRow(updatedTaskRow).toEither)
    } yield writtenUpdatedTask

    transformer.value.map(Validated.fromEither)
  }

  def updateTaskProgressC(taskKey: TaskKey, reachedValue: Natural): ConnectionIO[ServerError.Valid[Task]] = ???

}
