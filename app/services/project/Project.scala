package services.project

import db.generated.Tables
import db.{ ProjectId, UserId }
import io.scalaland.chimney.Transformer

import java.time.LocalDateTime

case class Project(
    id: ProjectId,
    name: String,
    description: Option[String],
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)

object Project {
  implicit val fromDB: Transformer[Tables.ProjectRow, Project]         = ???
  implicit val toDB: Transformer[(Project, UserId), Tables.ProjectRow] = ???
}
