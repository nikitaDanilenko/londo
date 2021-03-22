package db.generated

import scala.meta._

sealed trait KeyDescription {
  def compareKeys(arg: String, key: String): Term
  def keyType: Type
}

object KeyDescription {

  case class Column1(
      columnName: String,
      columnType: String
  ) extends KeyDescription {

    override def compareKeys(arg: String, key: String): Term =
      TermUtils.equality(arg, columnName, Term.Name(key))

    override def keyType: Type = Type.Name(columnType)
  }

  case class Column2(
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

}
