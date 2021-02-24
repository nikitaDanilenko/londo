package db.models

case class Task(
    id: java.util.UUID,
    projectId: java.util.UUID,
    name: String,
    unit: Option[String],
    kindId: java.util.UUID,
    reached: Int,
    reachable: Int,
    weight: Int
)
