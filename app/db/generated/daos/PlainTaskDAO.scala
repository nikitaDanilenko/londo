package db.generated.daos

import cats.effect.{ Async, ContextShift }
import cats.syntax.applicativeError._
import db.models._
import db.keys._
import db.{ DbContext, DbTransactorProvider, DAOFunctions }
import doobie.ConnectionIO
import doobie.implicits._
import io.getquill.ActionReturning
import java.util.UUID
import javax.inject.Inject

class PlainTaskDAO @Inject() (dbContext: DbContext, override protected val dbTransactorProvider: DbTransactorProvider)
    extends DAOFunctions[PlainTask, PlainTaskDAO.Key] {
  import dbContext._
  override def findC(key: PlainTaskDAO.Key): ConnectionIO[Option[PlainTask]] = run(findAction(key)).map(_.headOption)
  override def insertC(row: PlainTask): ConnectionIO[Either[Throwable, PlainTask]] = run(insertAction(row)).attempt

  override def insertAllC(rows: Seq[PlainTask]): ConnectionIO[Either[Throwable, List[PlainTask]]] =
    run(insertAllAction(rows)).attempt

  override def deleteC(key: PlainTaskDAO.Key): ConnectionIO[Either[Throwable, PlainTask]] =
    run(deleteAction(key)).attempt

  override def replaceC(row: PlainTask): ConnectionIO[Either[Throwable, PlainTask]] = run(replaceAction(row)).attempt

  private def findAction(key: PlainTaskDAO.Key) =
    quote {
      PublicSchema.PlainTaskDao.query.filter(a => a.projectId == lift(key.projectId.uuid) && a.id == lift(key.uuid))
    }

  private def insertAction(row: PlainTask): Quoted[ActionReturning[PlainTask, PlainTask]] =
    quote {
      PublicSchema.PlainTaskDao.query.insert(lift(row)).returning(x => x)
    }

  private def insertAllAction(rows: Seq[PlainTask]) =
    quote {
      liftQuery(rows).foreach(e => PublicSchema.PlainTaskDao.query.insert(e).returning(x => x))
    }

  private def deleteAction(key: PlainTaskDAO.Key) =
    quote {
      findAction(key).delete.returning(x => x)
    }

  private def replaceAction(row: PlainTask) = {
    quote {
      PublicSchema.PlainTaskDao.query
        .insert(lift(row))
        .onConflictUpdate(_.projectId, _.id)(
          (t, e) => t.id -> e.id,
          (t, e) => t.projectId -> e.projectId,
          (t, e) => t.name -> e.name,
          (t, e) => t.unit -> e.unit,
          (t, e) => t.kindId -> e.kindId,
          (t, e) => t.reached -> e.reached,
          (t, e) => t.reachable -> e.reachable,
          (t, e) => t.weight -> e.weight
        )
        .returning(x => x)
    }
  }

  def findById[F[_]: Async: ContextShift](key: UUID): F[List[PlainTask]] = {
    findByIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByIdC(key: UUID): ConnectionIO[List[PlainTask]] = {
    run(findByIdAction(key))
  }

  def deleteById[F[_]: Async: ContextShift](key: UUID): F[Either[Throwable, Long]] = {
    deleteByIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByIdC(key: UUID): ConnectionIO[Either[Throwable, Long]] = {
    run(deleteByIdAction(key)).attempt
  }

  def findByProjectId[F[_]: Async: ContextShift](key: UUID): F[List[PlainTask]] = {
    findByProjectIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def findByProjectIdC(key: UUID): ConnectionIO[List[PlainTask]] = {
    run(findByProjectIdAction(key))
  }

  def deleteByProjectId[F[_]: Async: ContextShift](key: UUID): F[Either[Throwable, Long]] = {
    deleteByProjectIdC(key).transact(dbTransactorProvider.transactor[F])
  }

  def deleteByProjectIdC(key: UUID): ConnectionIO[Either[Throwable, Long]] = {
    run(deleteByProjectIdAction(key)).attempt
  }

  private def findByIdAction(key: UUID) = {
    quote {
      PublicSchema.PlainTaskDao.query.filter(a => a.id == lift(key))
    }
  }

  private def deleteByIdAction(key: UUID) = {
    quote {
      findByIdAction(key).delete
    }
  }

  private def findByProjectIdAction(key: UUID) = {
    quote {
      PublicSchema.PlainTaskDao.query.filter(a => a.projectId == lift(key))
    }
  }

  private def deleteByProjectIdAction(key: UUID) = {
    quote {
      findByProjectIdAction(key).delete
    }
  }

}

object PlainTaskDAO { type Key = TaskId }
