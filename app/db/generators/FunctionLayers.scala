package db.generators

import scala.meta.Term

sealed trait FunctionLayers {

  def function: Term.Name
  def functionF: Term.Name
  def functionAction: Term.Name
}

object FunctionLayers {

  private case class FunctionLayersImpl(
      override val function: Term.Name,
      override val functionF: Term.Name,
      override val functionAction: Term.Name
  ) extends FunctionLayers

  private def functionName(prefix: String, field: String)(suffix: String): Term.Name =
    Term.Name(List(prefix, field, suffix).mkString)

  def apply(prefix: String, field: String): FunctionLayers = {
    val mkName: String => Term.Name = functionName(prefix, field)
    FunctionLayersImpl(
      function = mkName(""),
      functionF = mkName("F"),
      functionAction = mkName("Action")
    )
  }

}
