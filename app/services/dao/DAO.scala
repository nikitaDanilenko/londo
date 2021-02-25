package services.dao

import cats.data.EitherT
import cats.effect.{ ContextShift, IO }
import db.DbContext
import doobie.implicits._
import errors.DBError
import io.getquill.{ ActionReturning, BatchAction, EntityQuery, Insert }

trait DAO[Row, Model, Key] {
  val dbContext: DbContext
  import dbContext._

  implicit val rowEncoder: Encoder[Row]
  implicit val rowDecoder: Decoder[Row]
  implicit def schemaMeta: SchemaMeta[Row]

  def schema: EntityQuery[Row]
  def keyOf: Row => Key
  def schemaName: String

  def insertAction(row: Row) = {
    quote {
      query[Row].insert(lift(row)).returning(_ => row)
    }
  }

  def insert(row: Row)(implicit contextShift: ContextShift[IO]) =
    run(insertAction(row)).transact(transactor[IO])

  def insertAllAction(rows: Seq[Row]) =
    quote {
      liftQuery(rows).foreach(query[Row].insert(_))
    }

//
//  def insertAll(models: Seq[Row])(implicit contextShift: ContextShift[IO]) =
//    run(insertAllAction(models)).transact(transactor)

//
//  def findAction(key: Key): EntityQuery[Model] =
//    findQuery(key).map(fromRow)
//
//  def findOption(key: Key)(implicit contextShift: ContextShift[IO]): IO[Option[Model]] =
//    run(findAction(key)).transact(transactor).map(_.headOption)
//
//  def find(key: Key)(implicit contextShift: ContextShift[IO]): IO[Model] =
//    EitherT
//      .fromOptionF(findOption(key), DBError.EntityNotFound(s"No $schemaName found with the given key = $key"))
//      .value
//      .flatMap(IO.fromEither)
//
//  def findAllAction(keys: Seq[Key]): EntityQuery[Model] = {
//    val idSet = keys.toSet
//    schema.filter(row => idSet.contains(keyOf(row))).map(fromRow)
//  }
//
//  def findAll(keys: Seq[Key])(implicit contextShift: ContextShift[IO]): IO[List[Model]] =
//    run(findAllAction(keys)).transact(transactor)
//
//  def deleteAction(key: Key): ActionReturning[Row, Model] =
//    findQuery(key).delete.returning(fromRow)
//
//  def delete(key: Key)(implicit contextShift: ContextShift[IO]): IO[Model] = run(deleteAction(key)).transact(transactor)
//
//  def deleteAllAction(keys: Seq[Key]): BatchAction[ActionReturning[Row, Model]] = {
//    liftQuery(keys).foreach(deleteAction)
//  }
//
//  def deleteAll(keys: Seq[Key])(implicit contextShift: ContextShift[IO]): IO[List[Model]] =
//    run(deleteAllAction(keys)).transact(transactor)
//
//  def updateAction(model: Model): ActionReturning[Row, Model] = {
//    val row = toRow(model)
//    findQuery(keyOf(row)).update(row).returning(fromRow)
//  }
//
//  def update(model: Model)(implicit contextShift: ContextShift[IO]): IO[Model] =
//    run(updateAction(model)).transact(transactor)
//
//  private def findQuery(key: Key): EntityQuery[Row] = schema.filter(keyOf(_) == key)
}

object DAO {

  abstract class Instance[Row, Model, Key](
      _fromRow: Row => Model,
      _toRow: Model => Row,
      _keyOf: Row => Key,
      _schemaName: String
  ) extends DAO[Row, Model, Key] {
    override val keyOf: Row => Key = _keyOf
    override val schemaName: String = _schemaName
  }

}
