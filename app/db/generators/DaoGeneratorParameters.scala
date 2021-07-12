package db.generators

import scala.reflect.ClassTag

sealed trait DaoGeneratorParameters {
  def typeName: String
  def daoPackage: String
  def keyDescription: KeyDescription
  def columnSearches: List[Column]
  def fieldNames: List[String]
}

object DaoGeneratorParameters {

  private case class DaoGeneratorParametersImpl(
      override val typeName: String,
      override val daoPackage: String,
      override val keyDescription: KeyDescription,
      override val columnSearches: List[Column],
      override val fieldNames: List[String]
  ) extends DaoGeneratorParameters

  def apply[A](
      daoPackage: String,
      keyDescription: KeyDescription,
      columnSearches: List[Column]
  )(implicit
      classTag: ClassTag[A]
  ): DaoGeneratorParameters = {
    DaoGeneratorParametersImpl(
      typeName = classTag.runtimeClass.getSimpleName,
      daoPackage = daoPackage,
      keyDescription = keyDescription,
      columnSearches = columnSearches,
      fieldNames = TermUtils.fieldList[A]
    )
  }

}
