package db

import cats.effect.{ ContextShift, IO }
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.context.Context
import io.getquill.{ PostgresDialect, SnakeCase }

import javax.inject.{ Inject, Singleton }

@Singleton
class DbContext @Inject() (dbConnection: DbConnection)
    extends DoobieContext.Postgres(SnakeCase: SnakeCase)
    with PublicExtensions[PostgresDialect, SnakeCase]
    with Context[PostgresDialect, SnakeCase] {

  // TODO: Should the transactor be constructed here via injection?
  def transactor(implicit contextShift: ContextShift[IO]): Transactor[IO] =
    Transactor.fromDriverManager[IO](
      driver = dbConnection.driver,
      url = dbConnection.url,
      user = dbConnection.userName,
      pass = dbConnection.password
    )

}
