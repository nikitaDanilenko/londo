package db

import cats.effect.{ Async, ContextShift }
import doobie.util.transactor.Transactor

import javax.inject.{ Inject, Singleton }

@Singleton
class DbTransactorProvider @Inject() (dbConnection: DbConnection) {

  def transactor[F[_]: Async: ContextShift]: Transactor[F] =
    Transactor.fromDriverManager[F](
      driver = dbConnection.driver,
      url = dbConnection.url,
      user = dbConnection.userName,
      pass = dbConnection.password
    )

}
