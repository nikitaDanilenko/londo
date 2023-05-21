package services.session

import db.{ SessionId, UserId }
import db.generated.Tables
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.util.UUID

case class Session(
    id: SessionId,
    createdAt: LocalDateTime
)

object Session {

  implicit val fromDB: Transformer[Tables.SessionRow, Session] =
    Transformer
      .define[Tables.SessionRow, Session]
      .buildTransformer

  implicit val toDB: Transformer[(Session, UserId), Tables.SessionRow] = { case (session, userId) =>
    Tables.SessionRow(
      id = session.id.transformInto[UUID],
      userId = userId.transformInto[UUID],
      createdAt = session.createdAt.transformInto[java.sql.Timestamp]
    )
  }

}
