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

  private val injector = new GuiceApplicationBuilder().injector()

  private val configuration = injector.instanceOf[Configuration]

  private val dbConfig = configuration.get[Config]("db.default")

  val dataSource = new PGSimpleDataSource()

  dataSource.setUser(dbConfig.getString("username"))
  dataSource.setPassword(dbConfig.getString("password"))

  private val cg: ComposeableTraitsJdbcCodegen =
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
    val paths = Future.sequence(cg.writeFiles("app/db/models"))
    val finished = Await.result(paths, Duration(10, TimeUnit.SECONDS))
    pprint.log(finished)
  }

}
