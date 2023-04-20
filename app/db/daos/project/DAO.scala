package db.daos.project

import db.{ DAOActions, ProjectId, UserId }
import db.generated.Tables
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.ProjectRow, ProjectKey] {

  override val keyOf: Tables.ProjectRow => ProjectKey = ProjectKey.of

  def findAllFor(userId: UserId): DBIO[Seq[Tables.ProjectRow]]

  def allOf(userId: UserId, ids: Seq[ProjectId]): DBIO[Seq[Tables.ProjectRow]]
}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.ProjectRow, Tables.Project, ProjectKey](
      Tables.Project,
      (table, key) =>
        table.ownerId === key.ownerId.transformInto[UUID] && table.id === key.projectId.transformInto[UUID]
    ) with DAO {

      override def findAllFor(userId: UserId): DBIO[Seq[Tables.ProjectRow]] =
        Tables.Project
          .filter(
            _.ownerId === userId.transformInto[UUID]
          )
          .result

      override def allOf(userId: UserId, ids: Seq[ProjectId]): DBIO[Seq[Tables.ProjectRow]] = {
        val untypedIds = ids.distinct.map(_.transformInto[UUID])
        Tables.Project
          .filter(project => project.ownerId === userId.transformInto[UUID] && project.id.inSetBind(untypedIds))
          .result
      }

    }

}
