package utils

import cats.syntax.contravariantSemigroupal._
import cats.{ Monad, StackSafeMonad }
import io.scalaland.chimney.dsl._
import slick.dbio.DBIO
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Rep

import java.time.LocalDate
import scala.concurrent.ExecutionContext

object DBIOUtil {

  object instances {

    implicit def dbioMonad(implicit executionContext: ExecutionContext): Monad[DBIO] =
      new StackSafeMonad[DBIO] {
        override def flatMap[A, B](fa: DBIO[A])(f: A => DBIO[B]): DBIO[B] = fa.flatMap(f)

        override def pure[A](x: A): DBIO[A] = DBIO.successful(x)
      }

  }

  // TODO: Check use
  def dateFilter(from: Option[LocalDate], to: Option[LocalDate]): Rep[java.sql.Date] => Rep[Boolean] = {
    val startFilter: LocalDate => Rep[java.sql.Date] => Rep[Boolean] = start => _ >= start.transformInto[java.sql.Date]

    val endFilter: LocalDate => Rep[java.sql.Date] => Rep[Boolean] = end => _ <= end.transformInto[java.sql.Date]

    (from, to) match {
      case (Some(start), Some(end)) => (startFilter(start), endFilter(end)).mapN(_ && _)
      case (Some(start), _)         => startFilter(start)
      case (_, Some(end))           => endFilter(end)
      case _                        => _ => true
    }
  }

}
