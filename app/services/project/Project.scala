package services.project

import db.{ ProjectId, UserId }

import java.util.Date

case class Project(
    id: ProjectId,
    name: String,
    description: Option[String],
    ownerId: UserId,
    createdAt: Date,
    updatedAt: Option[Date]
)
