package db.models

case class Task(
    id: java.util.UUID,
    projectId: java.util.UUID,
    projectReferenceId: Option[java.util.UUID],
    name: Option[String],
    unit: Option[String],
    kindId: Option[java.util.UUID],
    reached: Option[scala.math.BigDecimal],
    reachable: Option[scala.math.BigDecimal],
    weight: Int
)
