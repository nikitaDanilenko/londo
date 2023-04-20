package services.task.reference

import db.{ ProjectId, ReferenceTaskId }

import java.util.Date

case class ReferenceTask(
    id: ReferenceTaskId,
    projectReferenceId: ProjectId,
    createdAt: Date
)
