package db.models

case class ProjectAccess(projectId: java.util.UUID, userId: java.util.UUID, writeAllowed: Boolean)
