package db.generators

import better.files._
import com.typesafe.config.Config
import io.getquill.codegen.jdbc.ComposeableTraitsJdbcCodegen
import io.getquill.codegen.model._
import org.postgresql.ds.PGSimpleDataSource
import org.scalafmt.interfaces.Scalafmt
import play.api.Configuration
import play.api.inject.guice.GuiceApplicationBuilder

import java.util.UUID
import java.util.concurrent.TimeUnit
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.jdk.CollectionConverters._
import scala.meta._
import scala.reflect.classTag

object CodeGenerator {

  private val appFolder = "app"
  private val dbFolder = "db"
  private val modelsFolder = "models"

  private val pathToModelsParts = Vector(appFolder, dbFolder, modelsFolder)
  private val queriesFileParts = Vector(appFolder, dbFolder, "PublicExtensions.scala")

  private val pathToModels: String = pathToModelsParts.mkString("/")
  private val queriesFile: String = queriesFileParts.mkString("/")
  private val modelsPackageName: String = pathToModelsParts.tail.mkString(".")
  private val ignoredTablesKey: String = "ignoredTables"

  private val dbConfig: Config =
    new GuiceApplicationBuilder()
      .injector()
      .instanceOf[Configuration]
      .get[Config]("db.default")

  private val dataSource: PGSimpleDataSource = {
    val ds = new PGSimpleDataSource()
    ds.setUser(dbConfig.getString("username"))
    ds.setPassword(dbConfig.getString("password"))
    ds
  }

  private val ignoredTables = dbConfig.getStringList(ignoredTablesKey).asScala.toSet

  private val codeGenerator: ComposeableTraitsJdbcCodegen =
    new ComposeableTraitsJdbcCodegen(
      dataSource = dataSource,
      packagePrefix = modelsPackageName,
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

      override def stereotype(
          schemas: Seq[RawSchema[JdbcTableMeta, JdbcColumnMeta]]
      ): Seq[TableStereotype[JdbcTableMeta, JdbcColumnMeta]] = {
        val nonIgnoredTables = schemas.filter(rs => !ignoredTables.contains(rs.table.tableName))
        super.stereotype(nonIgnoredTables)
      }

    }

  private val format: String => String = {
    val scalafmt = Scalafmt.create(getClass.getClassLoader)
    val config = File(".scalafmt.conf")
    val file = File("CodeGen.scala")

    scalafmt
      .format(config.path, file.path, _)
  }

  private def readGenerated(files: Seq[File]): Generated = {
    //Separate into the case classes and everything else
    val (caseClasses, everythingElse) =
      files
        .map(_.contentAsString)
        .mkString("\n")
        .linesIterator
        .partition(_.trim.startsWith("case class"))
    Generated(
      caseClasses = caseClasses,
      everythingElse = everythingElse
    )
  }

  private def replaceCaseClasses(caseClasses: Iterator[String]): Unit = {
    caseClasses.foreach { caseClass =>
      val caseClassStat = caseClass.parse[Stat].get
      val caseClassName = caseClassStat.collect {
        case q"case class $tname (...$paramss)" => tname.value
      }.head

      val caseClassFile = pathToModels / s"$caseClassName.scala"

      if (caseClassFile.exists) {
        val originalContent = caseClassFile.contentAsString.parse[Source].get
        val withReplaced = replaceCode(originalContent, caseClassStat, caseClassName).toString()
        caseClassFile.overwrite(format(withReplaced))
      } else {
        val newContent = List(
          s"package $modelsPackageName",
          caseClass
        ).mkString("\n")
        caseClassFile.write(format(newContent))
      }
    }
  }

  private def replaceCode(originalFile: Source, caseClass: Stat, caseClassName: String): Tree = {
    originalFile.transform {
      //This is the complete case class pattern (with all possible options)
      case q"@$annot case class $tname (...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }"
          if tname.value == caseClassName =>
        caseClass.transform {
          //We assume that the given case class consists only of a name and parameters, and copy everything else from the original case class
          case q"case class $sameName (...$newParamss)" =>
            q"@$annot case class $sameName (...$newParamss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }"
        }
      //This is the case class pattern without annotations
      case q"case class $tname (...$paramss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }"
          if tname.value == caseClassName =>
        caseClass.transform {
          //We assume that the given case class consists only of a name and parameters, and copy everything else from the original case class
          case q"case class $sameName (...$newParamss)" =>
            q"case class $sameName (...$newParamss) extends { ..$earlydefns } with ..$parents { $self => ..$stats }"
        }
    }
  }

  private def rearrangeGenerated(paths: Seq[String]): Unit = {
    val files = paths.map(p => pathToModels / "public" / p)
    val generated = readGenerated(files)
    replaceCaseClasses(generated.caseClasses)
    File(queriesFile).overwrite {
      val newContent = {
        (s"package $dbFolder" +:
          s"import ${pathToModelsParts.tail.mkString(".")}._" +:
          generated.everythingElse.toVector.drop(1)).mkString("\n")
      }
      format(newContent)
    }
    files.foreach(_.delete())
  }

  private case class Generated(caseClasses: Iterator[String], everythingElse: Iterator[String])

  def main(args: Array[String]): Unit = {
    val files = codeGenerator.writeAllFiles(pathToModels).map(ps => rearrangeGenerated(ps.map(_.getFileName.toString)))
    Await.result(files, Duration(10, TimeUnit.SECONDS))
  }

}
