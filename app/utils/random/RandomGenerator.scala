package utils.random

import cats.effect.IO
import spire.math.Natural

import java.util.UUID
import scala.util.Random

object RandomGenerator {

  private val alphaNumericCharacters: List[Char] =
    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toList

  private def randomStringFrom(chars: List[Char], length: Natural): IO[String] =
    IO {
      val maxIndex = chars.length - 1
      val charMap  = chars.zipWithIndex.map(_.swap).toMap
      1.until(length.intValue)
        .map(_ => charMap(Random.nextInt(maxIndex)))
        .mkString
    }

  def randomString(length: Natural): IO[String] =
    IO(1.to(length.intValue).map(_ => Random.nextPrintableChar()).mkString)

  def randomAlphaNumericString(length: Natural): IO[String] =
    randomStringFrom(alphaNumericCharacters, length)

  def randomUUID: IO[UUID] =
    IO(UUID.randomUUID())

}
