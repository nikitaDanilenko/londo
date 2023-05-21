package services.loginThrottle

import db.UserId
import db.generated.Tables
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import utils.transformer.implicits._

import java.time.LocalDateTime
import java.util.UUID

case class LoginThrottle(
    failedAttempts: Int,
    lastAttemptAt: LocalDateTime
)

object LoginThrottle {

  implicit val fromDB: Transformer[Tables.LoginThrottleRow, LoginThrottle] =
    Transformer
      .define[Tables.LoginThrottleRow, LoginThrottle]
      .buildTransformer

  implicit val toDB: Transformer[(LoginThrottle, UserId), Tables.LoginThrottleRow] = { case (loginThrottle, userId) =>
    Tables.LoginThrottleRow(
      userId = userId.transformInto[UUID],
      failedAttempts = loginThrottle.failedAttempts,
      lastAttemptAt = loginThrottle.lastAttemptAt.transformInto[java.sql.Timestamp]
    )
  }

}
