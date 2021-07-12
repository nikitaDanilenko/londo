package db.models

case class PlainTask(
    id: java.util.UUID,
    projectId: java.util.UUID,
    name: String,
    unit: Option[String],
    kindId: java.util.UUID,
    reached: scala.math.BigDecimal,
    reachable: scala.math.BigDecimal,
    weight: Int
)
