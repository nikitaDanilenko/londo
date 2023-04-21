package db.daos.task

import db.generated.Tables
import db.{ DAOActions, ProjectId, TaskId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.PlainTaskRow, TaskId] {

  override val keyOf: Tables.PlainTaskRow => TaskId = _.id.transformInto[TaskId]

  def findAllFor(projectIds: Seq[ProjectId]): DBIO[Seq[Tables.PlainTaskRow]]

}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.PlainTaskRow, Tables.PlainTask, TaskId](
      Tables.PlainTask,
      (table, key) => table.id === key.transformInto[UUID]
    ) with DAO {

      override def findAllFor(projectIds: Seq[ProjectId]): DBIO[Seq[Tables.PlainTaskRow]] = {
        val untypedIds = projectIds.distinct.map(_.transformInto[UUID])
        Tables.PlainTask
          .filter(_.projectId.inSetBind(untypedIds))
          .result
      }

    }

}
