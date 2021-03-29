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
    val daoName = Type.Name(s"${typeName}DAO")
    val keyType = keyDescription.keyType
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
          ${functionNames.findLayers.functionF}(key).transact(dbTransactorProvider.transactor[F])
        }
        """,
        q"""
        def ${functionNames.findLayers.functionF}(key: ${column.typeTerm}): ConnectionIO[List[$typeName]] = {
          run(${functionNames.findLayers.functionAction}(key))
        }
        """,
        q"""
        def ${functionNames.deleteLayers.function}[F[_]: Async: ContextShift](key: ${column.typeTerm}): F[Long] = {
          ${functionNames.deleteLayers.functionF}(key).transact(dbTransactorProvider.transactor[F])
        }
        """,
        q"""
        def ${functionNames.deleteLayers.functionF}(key: ${column.typeTerm}): ConnectionIO[Long] = {
          run(${functionNames.deleteLayers.functionAction}(key))
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
          import db.models._
          import db.{DbContext, DbTransactorProvider}
          import doobie.ConnectionIO
          import doobie.implicits._
          import io.getquill.ActionReturning

          import java.util.UUID
          import javax.inject.Inject
          class $daoName @Inject()(
            dbContext: DbContext,
            dbTransactorProvider: DbTransactorProvider
          ) { 
            import dbContext._
            def find[F[_]: Async: ContextShift](key: $keyType): F[Option[$typeName]] =
              findF(key).transact(dbTransactorProvider.transactor[F])
            def findF(key: $keyType): ConnectionIO[Option[$typeName]] =
              run(findAction(key)).map(_.headOption)
            def insert[F[_]: Async: ContextShift](row: $typeName): F[$typeName] =
              insertF(row).transact(dbTransactorProvider.transactor[F])
            def insertF(row: $typeName): ConnectionIO[$typeName] =
              run(insertAction(row))
            def insertAll[F[_]: Async: ContextShift](rows: Seq[$typeName]): F[List[$typeName]] =
              insertAllF(rows).transact(dbTransactorProvider.transactor[F])
            def insertAllF(rows: Seq[$typeName]): ConnectionIO[List[$typeName]] =
              run(insertAllAction(rows))
            def delete[F[_]: Async: ContextShift](key: $keyType): F[$typeName] =
              deleteF(key).transact(dbTransactorProvider.transactor[F])
            def deleteF(key: $keyType): ConnectionIO[$typeName] =
              run(deleteAction(key))
            def replace[F[_]: Async: ContextShift](row: $typeName): F[$typeName] =
              run(replaceAction(row)).transact(dbTransactorProvider.transactor[F])
            def replaceF(row: $typeName): ConnectionIO[$typeName] =
              run(replaceAction(row))
    
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
          }}"""
    result
  }

}
