package db.generators

import cats.data.NonEmptyList

import scala.meta.{ Term, Type }

case class KeyCaseClass1(
    tpe: Type,
    field1: NonEmptyList[Term.Name]
)

object KeyCaseClass1 {

  def fromNames(className: String, fieldName: String, fieldNames: String*): KeyCaseClass1 =
    KeyCaseClass1(
      tpe = Type.Name(className),
      field1 = NonEmptyList.of(fieldName, fieldNames: _*).map(Term.Name.apply)
    )

}
