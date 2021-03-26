package db.generators

import scala.meta._

object GeneratedDAO {

  def create(
      typeName: Type.Name,
      daoPackage: String,
      keyDescription: KeyDescription,
      columnSearches: List[Column]
  ): Tree = {
    val daoPackageTerm = Pkg(ref = TermUtils.splitToTerm(daoPackage), stats = List.empty).ref
    val typeQuery = TermUtils.splitToTerm(s"PublicSchema.${typeName}Dao.query")
    val daoName = Type.Name(s"${typeName}DAO")
    val keyType = keyDescription.keyType

    def fromQuery(column: Column): List[Defn.Def] = {
      val field = column.name.capitalize
      val findByFunctionNameAction = Term.Name(s"findBy${field}Action")
      val findByFunctionName = Term.Name(s"findBy$field")
      val deleteByFunctionNameAction = Term.Name(s"deleteBy${field}Action")
      val deleteByFunctionName = Term.Name(s"deleteBy$field")
      List(
        q"""
        private def $findByFunctionNameAction(key: ${column.typeTerm}) = { 
          quote {
            $typeQuery.filter(a => ${TermUtils.equality("a", column.name, Term.Name("key"), column.mandatory)} )
          }
        }
        """,
        q"""
        def $findByFunctionName[F[_]: Async: ContextShift](key: ${column.typeTerm}): F[List[$typeName]] = {
          run($findByFunctionNameAction(key)).transact(transactor[F])
        }
        """,
        q"""private def $deleteByFunctionNameAction(key: ${column.typeTerm}) = {
          quote {
            $findByFunctionNameAction(key).delete
          }
        }
        """,
        q"""
        def $deleteByFunctionName[F[_]: Async: ContextShift](key: ${column.typeTerm}): F[Long] = {
          run($deleteByFunctionNameAction(key)).transact(transactor[F])
        }
       """
      )
    }

    val additional = columnSearches.flatMap(fromQuery)

    val result = q"""
          package $daoPackageTerm {
          import cats.effect.{ Async, ContextShift }
          import db.DbContext
          import db.models._
          import doobie.implicits._
          import io.getquill.ActionReturning
          import java.util.UUID
          import javax.inject.Inject
          class $daoName @Inject()(dbContext: DbContext) { 
            import dbContext._
            def find[F[_]: Async: ContextShift](key: $keyType): F[Option[$typeName]] =
              run(findAction(key)).map(_.headOption).transact(transactor[F])
            def insert[F[_]: Async: ContextShift](row: $typeName): F[$typeName] =
             run(insertAction(row)).transact(transactor[F])
            def insertAll[F[_]: Async: ContextShift](rows: Seq[$typeName]): F[List[$typeName]] =
              run(insertAllAction(rows)).transact(transactor[F])
            def delete[F[_]: Async: ContextShift](key: $keyType): F[$typeName] =
              run(deleteAction(key)).transact(transactor[F])
            def update[F[_]: Async: ContextShift](row: $typeName): F[$typeName] =
              run(updateAction(row)).transact(transactor[F])
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
            private def updateAction(row: $typeName) = 
              quote {
                $typeQuery
                  .update(lift(row))
                  .returning(x => x)
              }
              
            ..$additional
          }}"""
    result
  }

}
