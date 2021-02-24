package db.models

case class Project(
    id: java.util.UUID,
    name: String,
    description: Option[String],
    parentProjectId: Option[java.util.UUID]
)
