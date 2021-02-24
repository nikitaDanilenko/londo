package db

import com.typesafe.config.Config
import io.getquill.context.Context
import io.getquill.{ Literal, PostgresAsyncContext, PostgresDialect, SnakeCase }
import play.api.Configuration

import javax.inject.{ Inject, Singleton }

@Singleton
class DbContext @Inject() (configuration: Configuration)
    extends PostgresAsyncContext[SnakeCase](SnakeCase, configuration.get[Config]("quill.dataSource"))
    with PublicExtensions[PostgresDialect, Literal]
    with Context[PostgresDialect, Literal]
