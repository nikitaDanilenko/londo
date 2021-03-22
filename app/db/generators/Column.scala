package db.generators

import scala.meta.{ Term, Type }

sealed trait Column {
  def name: String
  def tpe: String
  def mandatory: Boolean

  final def nameTerm: Term.Name = Term.Name(name)
  final def typeTerm: Type.Name = Type.Name(tpe)
}

object Column {

  private case class ColumnImpl(
      override val name: String,
      override val tpe: String,
      override val mandatory: Boolean
  ) extends Column

  def uuid(name: String, mandatory: Boolean): Column =
    ColumnImpl(
      name = name,
      tpe = "UUID",
      mandatory = mandatory
    )

  def string(name: String, mandatory: Boolean): Column =
    ColumnImpl(
      name = name,
      tpe = "String",
      mandatory = mandatory
    )

}
