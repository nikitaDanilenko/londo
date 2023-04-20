package db.daos.task.reference

import db.generated.Tables
import db.{ DAOActions, ReferenceTaskId, ProjectId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.ReferenceTaskRow, ReferenceTaskId] {

  override val keyOf: Tables.ReferenceTaskRow => ReferenceTaskId = _.id.transformInto[ReferenceTaskId]

  def findAllFor(projectIds: Seq[ProjectId]): DBIO[Seq[Tables.ReferenceTaskRow]]

}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.ReferenceTaskRow, Tables.ReferenceTask, ReferenceTaskId](
      Tables.ReferenceTask,
      (table, key) => table.id === key.transformInto[UUID]
    ) with DAO {

      override def findAllFor(projectIds: Seq[ProjectId]): DBIO[Seq[Tables.ReferenceTaskRow]] = {
        val untypedIds = projectIds.distinct.map(_.transformInto[UUID])
        Tables.ReferenceTask
          .filter(_.projectId.inSetBind(untypedIds))
          .result
      }

    }

}
