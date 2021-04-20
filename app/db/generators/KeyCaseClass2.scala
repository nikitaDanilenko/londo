package db.generators

import cats.data.NonEmptyList

import scala.meta.{ Term, Type }

case class KeyCaseClass2(
    tpe: Type,
    field1: NonEmptyList[Term.Name],
    field2: NonEmptyList[Term.Name]
)

object KeyCaseClass2 {

  def fromNames(
      className: String
  )(fieldName1: String, fieldNames1: String*)(fieldName2: String, fieldNames2: String*): KeyCaseClass2 =
    KeyCaseClass2(
      tpe = Type.Name(className),
      field1 = NonEmptyList.of(fieldName1, fieldNames1: _*).map(Term.Name.apply),
      field2 = NonEmptyList.of(fieldName2, fieldNames2: _*).map(Term.Name.apply)
    )

}
