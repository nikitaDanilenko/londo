package db.generators

import scala.meta._

sealed trait KeyDescription {
  def compareKeys(arg: String, key: String): Term
  def keyType: Type
  def keyColumns: List[Term]
}

object KeyDescription {

  def column1(column: Column, keyCaseClass1: KeyCaseClass1): KeyDescription =
    new KeyDescription {

      override def compareKeys(arg: String, key: String): Term =
        TermUtils.equality(
          arg = arg,
          columnName = column.name,
          keyTerm = TermUtils.fieldTerms(key, keyCaseClass1.field1),
          mandatory = column.mandatory
        )

      override val keyType: Type = keyCaseClass1.tpe
      override val keyColumns: List[Term] = List(q"_.${column.nameTerm}")
    }

  def column2(column1: Column, column2: Column, keyCaseClass2: KeyCaseClass2): KeyDescription =
    new KeyDescription {

      override def compareKeys(arg: String, key: String): Term =
        Term.ApplyInfix(
          lhs = TermUtils.equality(
            arg = arg,
            columnName = column1.name,
            keyTerm = TermUtils.fieldTerms(key, keyCaseClass2.field1),
            mandatory = column1.mandatory
          ),
          op = Term.Name("&&"),
          targs = List.empty,
          args = List(
            TermUtils.equality(
              arg = arg,
              columnName = column2.name,
              keyTerm = TermUtils.fieldTerms(key, keyCaseClass2.field2),
              mandatory = column2.mandatory
            )
          )
        )

      override val keyType: Type = keyCaseClass2.tpe
      override val keyColumns: List[Term] = List(q"_.${column1.nameTerm}", q"_.${column2.nameTerm}")
    }

}
