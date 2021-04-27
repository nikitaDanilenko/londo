package services.task

import cats.effect.{ Async, ContextShift }
import db.Transactionally
import db.generated.daos.TaskDAO
import db.keys.ProjectId
import doobie.ConnectionIO
import errors.ServerError

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
      .findC(
        db.keys.TaskId(
          projectId = taskKey.projectId,
          uuid = taskKey.taskId.uuid
        )
      )
      .map(
        ServerError
          .fromOption(_, ServerError.Task.NotFound)
          .andThen(Task.fromRow)
      )

  def removeTask = ???
  def updateTask = ???

}
