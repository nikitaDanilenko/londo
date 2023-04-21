package services.session

import db.SessionId
import db.generated.Tables
import io.scalaland.chimney.Transformer

import java.time.LocalDateTime

case class Session(
    id: SessionId,
    createdAt: LocalDateTime
)

object Session {
  implicit val fromDB: Transformer[Tables.SessionRow, Session] = ???
  implicit val toDB: Transformer[Session, Tables.SessionRow]   = ???
}
