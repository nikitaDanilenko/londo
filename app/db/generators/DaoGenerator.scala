package db.generators

import better.files.File

import scala.meta.Type

object DaoGenerator {

  val daoPackage: String = "db.generated.daos"

  val daosToGenerate: Vector[DaoGeneratorParameters] = Vector(
    daoGeneratorParameters(
      typeName = "Dashboard",
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
    daoGeneratorParameters(
      typeName = "DashboardRestriction",
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "dashboardId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters(
      typeName = "Project",
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
    daoGeneratorParameters(
      typeName = "ProjectAccess",
      keyDescription = KeyDescription.column2(
        Column.uuid(
          name = "projectId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List(
        Column.uuid(
          name = "projectId",
          mandatory = true
        ),
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      )
    ),
    daoGeneratorParameters(
      typeName = "Task",
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
    daoGeneratorParameters(
      typeName = "TaskKind",
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "id",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters(
      typeName = "User",
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
    daoGeneratorParameters(
      typeName = "UserDetails",
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
      columnSearches = List.empty
    ),
    daoGeneratorParameters(
      typeName = "UserSettings",
      keyDescription = KeyDescription.column1(
        Column.uuid(
          name = "userId",
          mandatory = true
        )
      ),
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
