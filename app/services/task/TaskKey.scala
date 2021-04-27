package services.task

import db.keys.ProjectId
import io.circe.generic.JsonCodec

@JsonCodec
case class TaskKey(
    projectId: ProjectId,
    taskId: TaskId
)
