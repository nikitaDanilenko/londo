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
    transactionally {
      for {
        createdTask <- Async[ConnectionIO].liftIO(TaskCreation.create(taskCreation))
        taskRow = Task.toRow(projectId, createdTask)
        writtenTask <- taskDAO.insertC(taskRow)
      } yield Task.fromRow(writtenTask)
    }

  def removeTask = ???
  def updateTask = ???

}
