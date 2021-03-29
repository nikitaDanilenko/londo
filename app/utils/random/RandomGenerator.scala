package utils.random

import cats.effect.IO

import java.util.UUID
import scala.util.Random

object RandomGenerator {

  def randomString(length: Int): IO[String] =
    IO(1.to(length).map(_ => Random.nextPrintableChar()).mkString)

  def randomUUID: IO[UUID] =
    IO(UUID.randomUUID())

}
