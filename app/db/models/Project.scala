package db.models

case class Project(
    id: java.util.UUID,
    ownerId: java.util.UUID,
    name: String,
    description: Option[String],
    parentProjectId: Option[java.util.UUID],
    isReadRestricted: Boolean,
    isWriteRestricted: Boolean
)
