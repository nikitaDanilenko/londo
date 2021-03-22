package db.generators

import better.files.File

import scala.meta.Type

object DaoGenerator {

  val daoPackage: String = "db.generated.daos"

  val daosToGenerate: Vector[DaoGeneratorParameters] = Vector(
    daoGeneratorParameters(
      typeName = "Dashboard",
      keyDescription = KeyDescription.uuidColumn("dashboardId"),
      columnSearches = List(Column.uuid("userId"))
    ),
    daoGeneratorParameters(
      typeName = "DashboardRestriction",
      keyDescription = KeyDescription.uuidColumn("dashboardId"),
      columnSearches = List.empty
    ),
    daoGeneratorParameters(
      typeName = "Project",
      keyDescription = KeyDescription.uuidColumn("id"),
      columnSearches = List(
        Column.uuid("ownerId"),
        Column.string("name"),
        Column.uuid("parentProjectId")
      )
    ),
    daoGeneratorParameters(
      typeName = "ProjectAccess",
      keyDescription = KeyDescription.column2(Column.uuid("projectId"), Column.uuid("userId")),
      columnSearches = List(
        Column.uuid("projectId"),
        Column.uuid("userId")
      )
    ),
    daoGeneratorParameters(
      typeName = "Task",
      keyDescription = KeyDescription.column2(Column.uuid("id"), Column.uuid("projectId")),
      columnSearches = List(
        Column.uuid("id"),
        Column.uuid("projectId")
      )
    ),
    daoGeneratorParameters(
      typeName = "TaskKind",
      keyDescription = KeyDescription.column1(Column.uuid("id")),
      columnSearches = List.empty
    ),
    daoGeneratorParameters(
      typeName = "User",
      keyDescription = KeyDescription.uuidColumn("id"),
      columnSearches = List(
        Column.string("email"),
        Column.string("nickname")
      )
    ),
    daoGeneratorParameters(
      typeName = "UserDetails",
      keyDescription = KeyDescription.column1(Column.uuid("userId")),
      columnSearches = List.empty
    ),
    daoGeneratorParameters(
      typeName = "UserSettings",
      keyDescription = KeyDescription.column1(Column.uuid("userId")),
      columnSearches = List.empty
    )
  )

  private def daoGeneratorParameters(
      typeName: String,
      keyDescription: KeyDescription,
      columnSearches: List[Column]
  ): DaoGeneratorParameters =
    DaoGeneratorParameters(
      typeName = typeName,
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
        columnSearches = daoGeneratorParams.columnSearches
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
