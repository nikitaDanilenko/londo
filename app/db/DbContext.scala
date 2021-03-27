package db

import doobie.quill.DoobieContext
import io.getquill.context.Context
import io.getquill.{ CompositeNamingStrategy2, Escape, PostgresDialect, SnakeCase }

import javax.inject.{ Inject, Singleton }

@Singleton
class DbContext @Inject()
    extends DoobieContext.Postgres(CompositeNamingStrategy2[SnakeCase, Escape](SnakeCase, Escape))
    with PublicExtensions[PostgresDialect, CompositeNamingStrategy2[SnakeCase, Escape]]
    with Context[PostgresDialect, CompositeNamingStrategy2[SnakeCase, Escape]]
