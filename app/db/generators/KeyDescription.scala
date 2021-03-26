package db.generators

import scala.meta._

sealed trait KeyDescription {
  def compareKeys(arg: String, key: String): Term
  def keyType: Type
  def keyColumns: List[Term]
}

object KeyDescription {

  def column1(column: Column): KeyDescription =
    new KeyDescription {

      override def compareKeys(arg: String, key: String): Term =
        TermUtils.equality(
          arg = arg,
          columnName = column.name,
          keyTerm = Term.Name(key),
          mandatory = column.mandatory
        )

      override val keyType: Type = column.typeTerm
      override val keyColumns: List[Term] = List(q"_.${column.nameTerm}")
    }

  def column2(column1: Column, column2: Column): KeyDescription =
    new KeyDescription {

      override def compareKeys(arg: String, key: String): Term =
        Term.ApplyInfix(
          lhs = TermUtils.equality(
            arg = arg,
            columnName = column1.name,
            keyTerm = TermUtils.field(key, "_1"),
            mandatory = column1.mandatory
          ),
          op = Term.Name("&&"),
          targs = List.empty,
          args = List(
            TermUtils.equality(
              arg = arg,
              columnName = column2.name,
              keyTerm = TermUtils.field(key, "_2"),
              mandatory = column2.mandatory
            )
          )
        )

      override val keyType: Type = Type.Tuple(List(column1.typeTerm, column2.typeTerm))
      override val keyColumns: List[Term] = List(q"_.${column1.nameTerm}", q"_.${column2.nameTerm}")
    }

}
