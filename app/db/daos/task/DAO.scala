package db.daos.task

import db.generated.Tables
import db.{ DAOActions, ProjectId, TaskId }
import io.scalaland.chimney.dsl._
import slick.jdbc.PostgresProfile.api._
import utils.transformer.implicits._

import java.util.UUID

trait DAO extends DAOActions[Tables.TaskRow, TaskId] {

  override val keyOf: Tables.TaskRow => TaskId = _.id.transformInto[TaskId]

  def findAllFor(projectIds: Seq[ProjectId]): DBIO[Seq[Tables.TaskRow]]

}

object DAO {

  val instance: DAO =
    new DAOActions.Instance[Tables.TaskRow, Tables.Task, TaskId](
      Tables.Task,
      (table, key) => table.id === key.transformInto[UUID]
    ) with DAO {

      override def findAllFor(projectIds: Seq[ProjectId]): DBIO[Seq[Tables.TaskRow]] = {
        val untypedIds = projectIds.distinct.map(_.transformInto[UUID])
        Tables.Task
          .filter(_.projectId.inSetBind(untypedIds))
          .result
      }

    }

}
