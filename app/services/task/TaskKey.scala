package services.task

import db.keys.ProjectId
import io.circe.generic.JsonCodec

@JsonCodec
case class TaskKey(
    projectId: ProjectId,
    taskId: TaskId
)

object TaskKey {

  def toTaskId(taskKey: TaskKey): db.keys.TaskId =
    db.keys.TaskId(
      projectId = taskKey.projectId,
      uuid = taskKey.taskId.uuid
    )

}
