package services.loginAttempt

import db.UserId
import db.generated.Tables
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.util.UUID

case class LoginAttempt(
    failedAttempts: Int,
    lastSuccessfulLogin: Option[LocalDateTime]
)

object LoginAttempt {

  implicit val fromDB: Transformer[Tables.LoginAttemptRow, LoginAttempt] =
    Transformer
      .define[Tables.LoginAttemptRow, LoginAttempt]
      .buildTransformer

  implicit val toDB: Transformer[(LoginAttempt, UserId), Tables.LoginAttemptRow] = { case (loginAttempt, userId) =>
    Tables.LoginAttemptRow(
      userId = userId.transformInto[UUID],
      failedAttempts = loginAttempt.failedAttempts,
      lastSuccessfulLogin = loginAttempt.lastSuccessfulLogin.map(_.transformInto[java.sql.Timestamp])
    )
  }

}
