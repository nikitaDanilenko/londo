package db.daos.project

import db.generated.Tables
import db.{ ProjectId, UserId }
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

case class ProjectKey(
    ownerId: UserId,
    projectId: ProjectId
)

object ProjectKey {

  def of(row: Tables.ProjectRow): ProjectKey =
    ProjectKey(
      row.ownerId.transformInto[UserId],
      row.id.transformInto[ProjectId]
    )

}
