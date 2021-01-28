package db.models.public

import com.typesafe.config.Config
import io.getquill.{ PostgresAsyncContext, SnakeCase }
import play.api.Configuration

import javax.inject.{ Inject, Singleton }

@Singleton
class DbContext @Inject() (configuration: Configuration)
    extends PostgresAsyncContext[SnakeCase](SnakeCase, configuration.get[Config]("quill.dataSource"))
