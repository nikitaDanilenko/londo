package db.daos.task.reference

import db.generated.Tables
import db.{ DAOActions, ProjectReferenceTaskId, ProjectId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.ProjectReferenceTaskRow, ProjectReferenceTaskId] {

  override val keyOf: Tables.ProjectReferenceTaskRow => ProjectReferenceTaskId = _.id.transformInto[ProjectReferenceTaskId]

  def findAllFor(projectIds: Seq[ProjectId]): DBIO[Seq[Tables.ProjectReferenceTaskRow]]

}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.ProjectReferenceTaskRow, Tables.ProjectReferenceTask, ProjectReferenceTaskId](
      Tables.ProjectReferenceTask,
      (table, key) => table.id === key.transformInto[UUID]
    ) with DAO {

      override def findAllFor(projectIds: Seq[ProjectId]): DBIO[Seq[Tables.ProjectReferenceTaskRow]] = {
        val untypedIds = projectIds.distinct.map(_.transformInto[UUID])
        Tables.ProjectReferenceTask
          .filter(_.projectId.inSetBind(untypedIds))
          .result
      }

    }

}
