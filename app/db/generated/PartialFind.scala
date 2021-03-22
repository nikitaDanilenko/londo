package db.generated

import scala.meta._

sealed trait PartialFind {
  def columnName: Term.Name
  def columnType: Type.Name
}

object PartialFind {

  def apply(name: String, tpe: String): PartialFind =
    new PartialFind {
      override val columnName: Term.Name = Term.Name(name)
      override val columnType: Type.Name = Type.Name(tpe)
    }

}
