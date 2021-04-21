package db

import cats.effect.{ Async, ContextShift }
import doobie.ConnectionIO
import doobie.implicits._

trait DAOFunctions[Row, Key] {

  protected def dbTransactorProvider: DbTransactorProvider

  def findC(key: Key): ConnectionIO[Option[Row]]
  def insertC(row: Row): ConnectionIO[Row]
  def insertAllC(rows: Seq[Row]): ConnectionIO[List[Row]]
  def deleteC(key: Key): ConnectionIO[Row]
  def replaceC(row: Row): ConnectionIO[Row]

  final def find[F[_]: Async: ContextShift](key: Key): F[Option[Row]] =
    findC(key).transact(dbTransactorProvider.transactor[F])

  final def insert[F[_]: Async: ContextShift](row: Row): F[Row] =
    insertC(row).transact(dbTransactorProvider.transactor[F])

  final def insertAll[F[_]: Async: ContextShift](rows: Seq[Row]): F[List[Row]] =
    insertAllC(rows).transact(dbTransactorProvider.transactor[F])

  final def delete[F[_]: Async: ContextShift](key: Key): F[Row] =
    deleteC(key).transact(dbTransactorProvider.transactor[F])

  final def replace[F[_]: Async: ContextShift](row: Row): F[Row] =
    replaceC(row).transact(dbTransactorProvider.transactor[F])

}
