package services.session

import cats.effect.IO
import db.SessionId
import io.scalaland.chimney.dsl._
import utils.date.DateUtil
import utils.random.RandomGenerator
import utils.transformer.implicits._

object Creation {

  def create: IO[Session] = for {
    id  <- RandomGenerator.randomUUID.map(_.transformInto[SessionId])
    now <- DateUtil.now
  } yield Session(
    id = id,
    createdAt = now
  )

}
