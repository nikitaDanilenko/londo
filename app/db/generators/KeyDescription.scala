package db.generators

import scala.meta._

sealed trait KeyDescription {
  def compareKeys(arg: String, key: String): Term
  def keyType: Type
}

object KeyDescription {

  def column1(columnType: Column): KeyDescription = Column1(columnType.name, columnType.tpe)

  def column2(columnType1: Column, columnType2: Column): KeyDescription =
    Column2(
      columnName1 = columnType1.name,
      columnType1 = columnType1.tpe,
      columnName2 = columnType2.name,
      columnType2 = columnType2.tpe
    )

  private case class Column1(
      columnName: String,
      columnType: String
  ) extends KeyDescription {

    override def compareKeys(arg: String, key: String): Term =
      TermUtils.equality(arg, columnName, Term.Name(key))

    override val keyType: Type = Type.Name(columnType)
  }

  private case class Column2(
      columnName1: String,
      columnType1: String,
      columnName2: String,
      columnType2: String
  ) extends KeyDescription {

    override def compareKeys(arg: String, key: String): Term =
      Term.ApplyInfix(
        TermUtils.equality(arg, columnName1, TermUtils.field(key, "_1")),
        Term.Name("&&"),
        List.empty,
        List(TermUtils.equality(arg, columnName2, TermUtils.field(key, "_2")))
      )

    override val keyType: Type = Type.Tuple(List(Type.Name(columnType1), Type.Name(columnType2)))

  }

  def uuidColumn(columnName: String): Column1 = Column1(columnName, "UUID")
  def stringColumn(columnName: String): Column1 = Column1(columnName, "String")

}
