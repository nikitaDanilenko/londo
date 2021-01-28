package db

import com.typesafe.config.Config
import io.getquill.codegen.jdbc.ComposeableTraitsJdbcCodegen
import io.getquill.codegen.model.{ NameParser, SnakeCaseNames }
import org.postgresql.ds.PGSimpleDataSource
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder

import java.util.UUID
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, Future }
import scala.reflect.classTag

object CodeGenerator {

  private val dbConfig: Config =
    new GuiceApplicationBuilder()
      .injector()
      .instanceOf[Configuration]
      .get[Config]("quill.dataSource")

  private val dataSource: PGSimpleDataSource = {
    val ds = new PGSimpleDataSource()
    ds.setUser(dbConfig.getString("user"))
    ds.setPassword(dbConfig.getString("password"))
    ds
  }

  private val codeGenerator: ComposeableTraitsJdbcCodegen =
    new ComposeableTraitsJdbcCodegen(
      dataSource = dataSource,
      packagePrefix = "db.models",
      nestedTrait = true
    ) {

      override val nameParser: NameParser = new SnakeCaseNames {
        override val generateQuerySchemas: Boolean = true
      }

      override def typer: Typer = { jdbcTypeInfo =>
        jdbcTypeInfo.typeName match {
          case Some("uuid") => Some(classTag[UUID])
          case _            => super.typer(jdbcTypeInfo)
        }
      }

    }

  def main(args: Array[String]): Unit = {
    val paths = Future.sequence(codeGenerator.writeFiles("app/db/models"))
    Await.result(paths, Duration(10, TimeUnit.SECONDS))
  }

}
