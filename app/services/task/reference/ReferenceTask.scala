package services.task.reference

import db.{ ProjectId, ReferenceTaskId }

case class ReferenceTask(
    id: ReferenceTaskId,
    projectReferenceId: ProjectId
)

