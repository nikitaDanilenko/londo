package db.models

case class Dashboard(id: java.util.UUID, userId: java.util.UUID, header: String, description: Option[String])
