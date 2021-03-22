package db.generators

import scala.meta.Term

object TermUtils {
  def field(arg: String, name: String): Term = Term.Select(Term.Name(arg), Term.Name(name))

  def equality(arg: String, columnName: String, keyTerm: Term, mandatory: Boolean): Term = {
    val liftKeyTerm = Term.Apply(Term.Name("lift"), List(keyTerm))
    if (mandatory)
      Term.ApplyInfix(
        field(arg, columnName),
        Term.Name(if (mandatory) "==" else "==="),
        List.empty,
        List(liftKeyTerm)
      )
    else
      Term.Apply(Term.Select(field(arg, columnName), Term.Name("contains")), List(liftKeyTerm))
  }

  def splitToTerm(string: String): Term.Ref = {
    val parts = string.split("\\.").toVector
    val term = parts.tail.foldLeft(Term.Name(parts.head): Term.Ref) { (t, str) =>
      Term.Select(t, Term.Name(str))
    }
    term
  }

}
