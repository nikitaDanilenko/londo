package db.generators

import scala.meta._
import cats.syntax.arrow._

object GeneratedDAO {

  def create(
      typeName: Type.Name,
      daoPackage: String,
      keyDescription: KeyDescription,
      columnSearches: List[Column],
      fieldNames: List[String]
  ): Tree = {
    val daoPackageTerm = Pkg(ref = TermUtils.splitToTerm(daoPackage), stats = List.empty).ref
    val typeQuery = TermUtils.splitToTerm(s"PublicSchema.${typeName}Dao.query")
    val daoNameString = s"${typeName}DAO"
    val daoName = Type.Name(daoNameString)
    val daoNameCompanion = Term.Name(daoNameString)
    val keyType = Type.Select(Term.Name(daoNameString): Term.Ref, Type.Name("Key"))
    val allSelectors = fieldNames.map { fieldName =>
      val fieldTerm = Term.Name(fieldName)
      q"(t, e) => t.$fieldTerm -> e.$fieldTerm"
    }

    def fromQuery(column: Column): (List[Defn.Def], List[Defn.Def]) = {
      val field = column.name.capitalize
      val functionNames = FunctionNames(s"By$field")
      val publicFunctions = List(
        q"""
        def ${functionNames.findLayers.function}[F[_]: Async: ContextShift](key: ${column.typeTerm}): F[List[$typeName]] = {
          ${functionNames.findLayers.functionC}(key).transact(dbTransactorProvider.transactor[F])
        }
        """,
        q"""
        def ${functionNames.findLayers.functionC}(key: ${column.typeTerm}): ConnectionIO[List[$typeName]] = {
          run(${functionNames.findLayers.functionAction}(key))
        }
        """,
        q"""
        def ${functionNames.deleteLayers.function}[F[_]: Async: ContextShift](key: ${column.typeTerm}): F[Either[Throwable, Long]] = {
          ${functionNames.deleteLayers.functionC}(key).transact(dbTransactorProvider.transactor[F])
        }
        """,
        q"""
        def ${functionNames.deleteLayers.functionC}(key: ${column.typeTerm}): ConnectionIO[Either[Throwable, Long]] = {
          run(${functionNames.deleteLayers.functionAction}(key)).attempt
        }
        """
      )

      val privateFunctions = List(
        q"""
        private def ${functionNames.findLayers.functionAction}(key: ${column.typeTerm}) = {
          quote {
            $typeQuery.filter(a => ${TermUtils.equality("a", column.name, Term.Name("key"), column.mandatory)} )
          }
        }
        """,
        q"""private def ${functionNames.deleteLayers.functionAction}(key: ${column.typeTerm}) = {
          quote {
            ${functionNames.findLayers.functionAction}(key).delete
          }
        }
        """
      )
      (publicFunctions, privateFunctions)
    }

    val flatten: List[List[Defn.Def]] => List[Defn.Def] = _.flatten
    val (publicAdditional, privateAdditional) = (flatten *** flatten)(columnSearches.map(fromQuery).unzip)

    val result = q"""
          package $daoPackageTerm {
          import cats.effect.{Async, ContextShift}
          import cats.syntax.applicativeError._
          import db.models._
          import db.keys._
          import db.{DbContext, DbTransactorProvider, DAOFunctions}
          import doobie.ConnectionIO
          import doobie.implicits._
          import io.getquill.ActionReturning

          import java.util.UUID
          import javax.inject.Inject
          class $daoName @Inject()(
            dbContext: DbContext,
            override protected val dbTransactorProvider: DbTransactorProvider
          ) extends DAOFunctions[$typeName, $keyType] {
            import dbContext._
            
            override def findC(key: $keyType): ConnectionIO[Option[$typeName]] =
              run(findAction(key)).map(_.headOption)
  
            override def insertC(row: $typeName): ConnectionIO[Either[Throwable, $typeName]] =
              run(insertAction(row)).attempt
  
            override def insertAllC(rows: Seq[$typeName]): ConnectionIO[Either[Throwable, List[$typeName]]] =
              run(insertAllAction(rows)).attempt
  
            override def deleteC(key: $keyType): ConnectionIO[Either[Throwable, $typeName]] =
              run(deleteAction(key)).attempt
  
            override def replaceC(row: $typeName): ConnectionIO[Either[Throwable, $typeName]] =
              run(replaceAction(row)).attempt
    
            private def findAction(key: $keyType) =
              quote {
                $typeQuery.filter(a => ${keyDescription.compareKeys("a", "key")})
              }
            private def insertAction(row: $typeName): Quoted[ActionReturning[$typeName, $typeName]] =
              quote {
                $typeQuery
                  .insert(lift(row))
                  .returning(x => x)
              }
            private def insertAllAction(rows: Seq[$typeName]) =
              quote {
                liftQuery(rows).foreach(e => $typeQuery.insert(e).returning(x => x))
              }
            private def deleteAction(key: $keyType) =
              quote {
                findAction(key).delete
                  .returning(x => x)
              }
            private def replaceAction(row: $typeName) = {
              quote {
                $typeQuery
                  .insert(lift(row))
                  .onConflictUpdate(..${keyDescription.keyColumns})(..$allSelectors)
                  .returning(x => x)
              }
            }
            ..$publicAdditional
            ..$privateAdditional
          }
          object $daoNameCompanion {
            type Key = ${keyDescription.keyType}
          }}
          """
    result
  }

}
