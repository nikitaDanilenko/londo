package db.generated

import scala.meta.Term

object TermUtils {
  def field(arg: String, name: String): Term = Term.Select(Term.Name(arg), Term.Name(name))

  def equality(arg: String, columnName: String, keyTerm: Term): Term = {
    Term.ApplyInfix(
      field(arg, columnName),
      Term.Name("=="),
      List.empty,
      List(Term.Apply(Term.Name("lift"), List(keyTerm)))
    )
  }

  def splitToTerm(string: String): Term.Ref = {
    val parts = string.split("\\.").toVector
    val term = parts.tail.foldLeft(Term.Name(parts.head): Term.Ref) { (t, str) =>
      Term.Select(t, Term.Name(str))
    }
    term
  }

}
