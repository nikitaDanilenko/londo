package db.generators

import cats.data.NonEmptyList

import scala.meta.{ Term, Type }
import scala.reflect.ClassTag

object TermUtils {
  def field(arg: String, name: String): Term             = fieldTerm(arg, Term.Name(name))
  def fieldTerm(arg: String, fieldName: Term.Name): Term = Term.Select(Term.Name(arg), fieldName)

  def fieldTerms(arg: String, subSelectors: NonEmptyList[Term.Name]): Term =
    subSelectors.tail.foldLeft(fieldTerm(arg, subSelectors.head))(Term.Select(_, _))

  def equality(arg: String, columnName: String, keyTerm: Term, mandatory: Boolean): Term = {
    val liftKeyTerm = Term.Apply(Term.Name("lift"), Term.ArgClause(List(keyTerm)))
    if (mandatory)
      Term.ApplyInfix(
        field(arg, columnName),
        Term.Name(if (mandatory) "==" else "==="),
        Type.ArgClause(List.empty),
        Term.ArgClause(List(liftKeyTerm))
      )
    else
      Term.Apply(Term.Select(field(arg, columnName), Term.Name("contains")), Term.ArgClause(List(liftKeyTerm)))
  }

  def splitToTerm(string: String): Term.Ref = {
    val parts = string.split("\\.").toVector
    val term = parts.tail.foldLeft(Term.Name(parts.head): Term.Ref) { (t, str) =>
      Term.Select(t, Term.Name(str))
    }
    term
  }

  def fieldList[A](implicit classTag: ClassTag[A]): List[String] = {
    classTag.runtimeClass.getDeclaredFields
      .map(_.getName)
      .toList
  }

}
