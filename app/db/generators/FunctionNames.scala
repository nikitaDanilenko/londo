package db.generators

sealed trait FunctionNames {

  def findLayers: FunctionLayers
  def deleteLayers: FunctionLayers

}

object FunctionNames {

  private case class FunctionNamesImpl(
      override val findLayers: FunctionLayers,
      override val deleteLayers: FunctionLayers
  ) extends FunctionNames

  def apply(field: String): FunctionNames = {
    val byPrefix: String => FunctionLayers = FunctionLayers(_, field)
    FunctionNamesImpl(
      byPrefix("find"),
      byPrefix("delete")
    )
  }

}
