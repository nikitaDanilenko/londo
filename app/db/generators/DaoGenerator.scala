package db.generators

import better.files.File
import db.models.{
  Dashboard,
  DashboardReadAccess,
  DashboardReadAccessEntry,
  DashboardWriteAccess,
  DashboardWriteAccessEntry,
  LoginAttempt,
  Project,
  ProjectReadAccess,
  ProjectReadAccessEntry,
  ProjectWriteAccess,
  ProjectWriteAccessEntry,
  RegistrationToken,
  SessionKey,
  Task,
  TaskKind,
  User,
  UserDetails
}
import services.user.UserSettings

import scala.meta.Type
import scala.reflect.ClassTag

object DaoGenerator {

  val daoPackage: String = "db.generated.daos"

  val daosToGenerate: Vector[DaoGeneratorParameters] = Vector(
    daoGeneratorParameters[Dashboard](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "id",
          mandatory = true
        )
      ),
      columnSearches = List(
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      )
    ),
    daoGeneratorParameters[DashboardReadAccess](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "dashboardId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters[DashboardReadAccessEntry](
      keyDescription = KeyDescription.column2(
        Column.uuid(
          name = "dashboardReadAccessId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List(
        Column.uuid(
          name = "dashboardReadAccessId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      )
    ),
    daoGeneratorParameters[DashboardWriteAccess](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "dashboardId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters[DashboardWriteAccessEntry](
      keyDescription = KeyDescription.column2(
        Column.uuid(
          name = "dashboardWriteAccessId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List(
        Column.uuid(
          name = "dashboardWriteAccessId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      )
    ),
    daoGeneratorParameters[Project](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "id",
          mandatory = true
        )
      ),
      columnSearches = List(
        Column.uuid(
          name = "ownerId",
          mandatory = true
        ),
        Column.string(
          name = "name",
          mandatory = true
        ),
        Column.uuid(
          name = "parentProjectId",
          mandatory = false
        )
      )
    ),
    daoGeneratorParameters[ProjectReadAccess](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "projectId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters[ProjectReadAccessEntry](
      keyDescription = KeyDescription.column2(
        Column.uuid(
          name = "projectReadAccessId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List(
        Column.uuid(
          name = "projectReadAccessId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      )
    ),
    daoGeneratorParameters[ProjectWriteAccess](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "projectId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters[ProjectWriteAccessEntry](
      keyDescription = KeyDescription.column2(
        Column.uuid(
          name = "projectWriteAccessId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List(
        Column.uuid(
          name = "projectWriteAccessId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      )
    ),
    daoGeneratorParameters[Task](
      keyDescription = KeyDescription.column2(
        Column.uuid(
          name = "id",
          mandatory = true
        ),
        Column.uuid(
          name = "projectId",
          mandatory = true
        )
      ),
      columnSearches = List(
        Column.uuid(
          name = "id",
          mandatory = true
        ),
        Column.uuid(
          name = "projectId",
          mandatory = true
        )
      )
    ),
    daoGeneratorParameters[TaskKind](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "id",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters[User](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "id",
          mandatory = true
        )
      ),
      columnSearches = List(
        Column.string(
          name = "email",
          mandatory = true
        ),
        Column.string(
          name = "nickname",
          mandatory = true
        )
      )
    ),
    daoGeneratorParameters[UserDetails](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters[UserSettings](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters[SessionKey](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters[RegistrationToken](
      keyDescription = KeyDescription.column1(
        Column.string(
          name = "email",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters[LoginAttempt](
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    )
  )

  private def daoGeneratorParameters[A: ClassTag](
      keyDescription: KeyDescription,
      columnSearches: List[Column]
  ): DaoGeneratorParameters =
    DaoGeneratorParameters(
      daoPackage = daoPackage,
      keyDescription = keyDescription,
      columnSearches = columnSearches
    )

  private def generate(daosToGenerate: Vector[DaoGeneratorParameters]): Unit = {
    daosToGenerate.foreach { daoGeneratorParams =>
      val tree = GeneratedDAO.create(
        typeName = Type.Name(daoGeneratorParams.typeName),
        daoPackage = daoGeneratorParams.daoPackage,
        keyDescription = daoGeneratorParams.keyDescription,
        columnSearches = daoGeneratorParams.columnSearches,
        fieldNames = daoGeneratorParams.fieldNames
      )
      val filePath =
        ("app" +: daoGeneratorParams.daoPackage.split("\\.").toVector :+ s"${daoGeneratorParams.typeName}DAO.scala")
          .mkString("/")
      pprint.log(s"Generated $filePath")
      File(filePath).createFileIfNotExists(createParents = true).write(Formatter.format(tree.toString()))
    }
  }

  def main(args: Array[String]): Unit = {
    generate(daosToGenerate)
  }

}
