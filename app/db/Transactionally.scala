package db

import cats.effect.{ Async, ContextShift }
import doobie.ConnectionIO
import doobie.implicits._

import javax.inject.Inject

class Transactionally @Inject() (dbTransactorProvider: DbTransactorProvider) {

  def apply[A, F[_]: Async: ContextShift](action: ConnectionIO[A]): F[A] =
    action.transact(dbTransactorProvider.transactor[F])

}
