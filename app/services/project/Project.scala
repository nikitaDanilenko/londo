package services.project

import db.{ ProjectId, UserId }

import java.time.LocalDateTime

case class Project(
    id: ProjectId,
    name: String,
    description: Option[String],
    ownerId: UserId,
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)
