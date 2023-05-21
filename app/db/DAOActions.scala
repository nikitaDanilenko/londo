package db

import slick.jdbc.PostgresProfile.api._
import slick.lifted.{ Rep, TableQuery }
import slick.relational.RelationalProfile

import scala.concurrent.ExecutionContext

trait DAOActions[Content, Key] {

  def keyOf: Content => Key

  def find(key: Key): DBIO[Option[Content]]

  def delete(key: Key): DBIO[Int]

  def insert(content: Content): DBIO[Content]

  def insertAll(contents: Seq[Content]): DBIO[Seq[Content]]
  def update(value: Content)(implicit ec: ExecutionContext): DBIO[Boolean]

  def exists(key: Key): DBIO[Boolean]

}

object DAOActions {

  abstract class Instance[Content, Table <: RelationalProfile#Table[Content], Key](
      table: TableQuery[Table],
      compare: (Table, Key) => Rep[Boolean]
  ) extends DAOActions[Content, Key] {

    override def find(key: Key): DBIO[Option[Content]] =
      findQuery(key).result.headOption

    override def delete(key: Key): DBIO[Int] =
      findQuery(key).delete

    override def insert(
        content: Content
    ): DBIO[Content] =
      table.returning(table) += content

    override def insertAll(
        contents: Seq[Content]
    ): DBIO[Seq[Content]] =
      table.returning(table) ++= contents

    override def update(value: Content)(implicit ec: ExecutionContext): DBIO[Boolean] =
      findQuery(keyOf(value))
        .update(value)
        .map(_ == 1)

    override def exists(key: Key): DBIO[Boolean] =
      findQuery(key).exists.result

    private def findQuery(key: Key): Query[Table, Content, Seq] =
      table.filter(compare(_, key))

  }

}
