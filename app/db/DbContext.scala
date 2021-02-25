package db

import cats.effect.{ Async, ContextShift }
import doobie.quill.DoobieContext
import doobie.util.transactor.Transactor
import io.getquill.context.Context
import io.getquill.{ CompositeNamingStrategy2, Escape, PostgresDialect, SnakeCase }
import services.dao.MacroDAO

import javax.inject.{ Inject, Singleton }

@Singleton
class DbContext @Inject() (dbConnection: DbConnection)
    extends DoobieContext.Postgres(CompositeNamingStrategy2[SnakeCase, Escape](SnakeCase, Escape))
    with PublicExtensions[PostgresDialect, CompositeNamingStrategy2[SnakeCase, Escape]]
    with Context[PostgresDialect, CompositeNamingStrategy2[SnakeCase, Escape]]
    with MacroDAO {

  // TODO: Should the transactor be constructed here via injection?
  def transactor[F[_]: Async](implicit contextShift: ContextShift[F]): Transactor[F] =
    Transactor.fromDriverManager[F](
      driver = dbConnection.driver,
      url = dbConnection.url,
      user = dbConnection.userName,
      pass = dbConnection.password
    )

}
