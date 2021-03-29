package utils.random

import cats.effect.IO

import java.util.UUID
import scala.util.Random

object RandomGenerator {

  def randomString(length: Int): IO[String] =
    IO(Random.nextString(length))

  def randomUUID: IO[UUID] =
    IO(UUID.randomUUID())

}
