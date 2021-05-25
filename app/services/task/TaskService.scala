package services.task

import cats.data.{ EitherT, NonEmptyList, Validated }
import cats.effect.{ Async, ContextShift, IO }
import db.Transactionally
import db.generated.daos.{ PlainTaskDAO, ProjectReferenceTaskDAO }
import db.keys.ProjectId
import doobie.ConnectionIO
import errors.ServerError
import services.project.ProgressUpdate

import javax.inject.Inject

class TaskService @Inject() (
    plainTaskDAO: PlainTaskDAO,
    projectReferenceTaskDAO: ProjectReferenceTaskDAO,
    transactionally: Transactionally
) {

  def createPlainTask[F[_]: Async: ContextShift](
      projectId: ProjectId,
      plainCreation: TaskCreation.Plain
  ): F[ServerError.Valid[Task.Plain]] =
    transactionally(createPlainTaskC(projectId, plainCreation))

  def createPlainTaskC(
      projectId: ProjectId,
      plainCreation: TaskCreation.Plain
  ): ConnectionIO[ServerError.Valid[Task.Plain]] =
    createTaskC(
      projectId = projectId,
      creation = plainCreation,
      toTask = TaskCreation.Plain.create,
      fromRow = Task.Plain.fromRow,
      toRow = Task.Plain.toRow,
      insert = plainTaskDAO.insertC
    )

  def createProjectReferenceTask[F[_]: Async: ContextShift](
      projectId: ProjectId,
      projectReferenceCreation: TaskCreation.ProjectReference
  ): F[ServerError.Valid[Task.ProjectReference]] =
    transactionally(createProjectReferenceTaskC(projectId, projectReferenceCreation))

  def createProjectReferenceTaskC(
      projectId: ProjectId,
      projectReferenceCreation: TaskCreation.ProjectReference
  ): ConnectionIO[ServerError.Valid[Task.ProjectReference]] =
    createTaskC(
      projectId = projectId,
      creation = projectReferenceCreation,
      toTask = TaskCreation.ProjectReference.create,
      fromRow = Task.ProjectReference.fromRow,
      toRow = Task.ProjectReference.toRow,
      insert = projectReferenceTaskDAO.insertC
    )

  def fetchPlain[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Valid[Task.Plain]] =
    transactionally(fetchPlainC(taskKey))

  def fetchPlainC(taskKey: TaskKey): ConnectionIO[ServerError.Valid[Task.Plain]] =
    fetchTaskC(taskKey, plainTaskDAO.findC, Task.Plain.fromRow)

  def fetchProjectReference[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Valid[Task.ProjectReference]] =
    transactionally(fetchProjectReferenceC(taskKey))

  def fetchProjectReferenceC(taskKey: TaskKey): ConnectionIO[ServerError.Valid[Task.ProjectReference]] =
    fetchTaskC(taskKey, projectReferenceTaskDAO.findC, Task.ProjectReference.fromRow)

  def removePlainTask[F[_]: Async: ContextShift](taskKey: TaskKey): F[ServerError.Valid[Task.Plain]] =
    transactionally(removePlainTaskC(taskKey))

  def removePlainTaskC(taskKey: TaskKey): ConnectionIO[ServerError.Valid[Task.Plain]] =
    removeTaskC(taskKey, plainTaskDAO.deleteC, Task.Plain.fromRow)

  def removeProjectReferenceTask[F[_]: Async: ContextShift](
      taskKey: TaskKey
  ): F[ServerError.Valid[Task.ProjectReference]] =
    transactionally(removeProjectReferenceTaskC(taskKey))

  def removeProjectReferenceTaskC(taskKey: TaskKey): ConnectionIO[ServerError.Valid[Task.ProjectReference]] =
    removeTaskC(taskKey, projectReferenceTaskDAO.deleteC, Task.ProjectReference.fromRow)

  def updatePlainTask[F[_]: Async: ContextShift](
      taskKey: TaskKey,
      plainUpdate: TaskUpdate.Plain
  ): F[ServerError.Valid[Task.Plain]] =
    transactionally(updatePlainTaskC(taskKey, plainUpdate))

  def updatePlainTaskC(taskKey: TaskKey, plainUpdate: TaskUpdate.Plain): ConnectionIO[ServerError.Valid[Task.Plain]] =
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
  ): F[ServerError.Valid[Task.ProjectReference]] =
    transactionally(updateProjectReferenceTaskC(taskKey, plainUpdate))

  def updateProjectReferenceTaskC(
      taskKey: TaskKey,
      plainUpdate: TaskUpdate.ProjectReference
  ): ConnectionIO[ServerError.Valid[Task.ProjectReference]] =
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
  ): F[ServerError.Valid[Task.Plain]] =
    transactionally(updateTaskProgressC(taskKey, progressUpdate))

  def updateTaskProgressC(
      taskKey: TaskKey,
      progressUpdate: ProgressUpdate
  ): ConnectionIO[ServerError.Valid[Task.Plain]] = {
    val transformer = for {
      task <- EitherT(fetchTaskC(taskKey, plainTaskDAO.findC, Task.Plain.fromRow).map(_.toEither))
      updatedTask <- replacePlainT(
        projectId = taskKey.projectId,
        plainTask = ProgressUpdate.applyToTask(task, progressUpdate)
      )
    } yield updatedTask

    transformer.value.map(Validated.fromEither)
  }

  private def replacePlainT(
      projectId: ProjectId,
      plainTask: Task.Plain
  ): EitherT[ConnectionIO, NonEmptyList[ServerError], Task.Plain] =
    replaceTaskT(
      projectId = projectId,
      task = plainTask,
      replace = plainTaskDAO.replaceC,
      fromRow = Task.Plain.fromRow,
      toRow = Task.Plain.toRow
    )

  private def replaceProjectReferenceT(
      projectId: ProjectId,
      projectReferenceTask: Task.ProjectReference
  ): EitherT[ConnectionIO, NonEmptyList[ServerError], Task.ProjectReference] =
    replaceTaskT(
      projectId = projectId,
      task = projectReferenceTask,
      replace = projectReferenceTaskDAO.replaceC,
      fromRow = Task.ProjectReference.fromRow,
      toRow = Task.ProjectReference.toRow
    )

  private def replaceTaskT[Task, Row](
      projectId: ProjectId,
      task: Task,
      replace: Row => ConnectionIO[Row],
      fromRow: Row => ServerError.Valid[Task],
      toRow: (ProjectId, Task) => Row
  ): EitherT[ConnectionIO, NonEmptyList[ServerError], Task] =
    for {
      writtenRow <- EitherT.liftF(replace(toRow(projectId, task)))
      writtenTask <- EitherT.fromEither[ConnectionIO](fromRow(writtenRow).toEither)
    } yield writtenTask

  private def createTaskC[Creation, Task, Row](
      projectId: ProjectId,
      creation: Creation,
      toTask: Creation => IO[Task],
      fromRow: Row => ServerError.Valid[Task],
      toRow: (ProjectId, Task) => Row,
      insert: Row => ConnectionIO[Row]
  ): ConnectionIO[ServerError.Valid[Task]] =
    for {
      createdTask <- Async[ConnectionIO].liftIO(toTask(creation))
      writtenTask <- insert(toRow(projectId, createdTask))
    } yield fromRow(writtenTask)

  private def fetchTaskC[Task, Row](
      taskKey: TaskKey,
      find: db.keys.TaskId => ConnectionIO[Option[Row]],
      fromRow: Row => ServerError.Valid[Task]
  ): ConnectionIO[ServerError.Valid[Task]] =
    find(TaskKey.toTaskId(taskKey))
      .map(
        ServerError
          .fromOption(_, ServerError.Task.NotFound)
          .andThen(fromRow)
      )

  private def updateTaskC[Task, Update, Row](
      taskKey: TaskKey,
      taskUpdate: Update,
      fetchC: TaskKey => ConnectionIO[ServerError.Valid[Task]],
      applyUpdate: (Task, Update) => Task,
      replaceTask: (ProjectId, Task) => EitherT[ConnectionIO, NonEmptyList[ServerError], Task]
  ): ConnectionIO[ServerError.Valid[Task]] = {
    val transformer = for {
      task <- EitherT(fetchC(taskKey).map(_.toEither))
      updatedTask <- replaceTask(taskKey.projectId, applyUpdate(task, taskUpdate))
    } yield updatedTask

    transformer.value.map(Validated.fromEither)
  }

  private def removeTaskC[Row, Task](
      taskKey: TaskKey,
      deleteC: db.keys.TaskId => ConnectionIO[Row],
      fromRow: Row => ServerError.Valid[Task]
  ): ConnectionIO[ServerError.Valid[Task]] =
    deleteC(TaskKey.toTaskId(taskKey))
      .map(fromRow)

}
