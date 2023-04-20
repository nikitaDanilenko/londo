package services.task.reference

import db.{ ProjectId, ReferenceTaskId }

import java.time.LocalDateTime

case class ReferenceTask(
    id: ReferenceTaskId,
    projectReferenceId: ProjectId,
    createdAt: LocalDateTime
)
