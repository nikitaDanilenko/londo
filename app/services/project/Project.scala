package services.project

import db.generated.Tables
import db.{ ProjectId, UserId }
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.util.UUID

case class Project(
    id: ProjectId,
    name: String,
    description: Option[String],
    createdAt: LocalDateTime,
    updatedAt: Option[LocalDateTime]
)

object Project {

  implicit val fromDB: Transformer[Tables.ProjectRow, Project] =
    Transformer
      .define[Tables.ProjectRow, Project]
      .buildTransformer

  implicit val toDB: Transformer[(Project, UserId), Tables.ProjectRow] = { case (project, ownerId) =>
    Tables.ProjectRow(
      id = project.id.transformInto[UUID],
      ownerId = ownerId.transformInto[UUID],
      name = project.name,
      description = project.description,
      createdAt = project.createdAt.transformInto[java.sql.Timestamp],
      updatedAt = project.updatedAt.map(_.transformInto[java.sql.Timestamp])
    )
  }

}
