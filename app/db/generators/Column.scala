package db.generators

import scala.meta.{ Term, Type }

sealed trait Column {
  def name: String
  def tpe: String

  final def nameTerm: Term.Name = Term.Name(name)
  final def typeTerm: Type.Name = Type.Name(tpe)
}

object Column {
  private case class ColumnImpl(override val name: String, override val tpe: String) extends Column

  def uuid(name: String): Column = ColumnImpl(name, "UUID")
  def string(name: String): Column = ColumnImpl(name, "String")
}
