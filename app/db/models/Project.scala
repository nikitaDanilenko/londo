package db.models

case class Project(
    id: java.util.UUID,
    ownerId: java.util.UUID,
    name: String,
    description: Option[String],
    flatIfSingleTask: Boolean
)
