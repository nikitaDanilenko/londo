package services.task

import services.project.ProjectId

case class TaskKey(
    projectId: ProjectId,
    taskId: TaskId
)

object TaskKey {

  def toTaskId(taskKey: TaskKey): db.keys.TaskId =
    db.keys.TaskId(
      projectId = ProjectId.toDb(taskKey.projectId),
      uuid = taskKey.taskId.uuid
    )

}
