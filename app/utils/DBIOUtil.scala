package utils

import cats.{ Monad, StackSafeMonad }
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext

object DBIOUtil {

  object instances {

    implicit def dbioMonad(implicit executionContext: ExecutionContext): Monad[DBIO] =
      new StackSafeMonad[DBIO] {
        override def flatMap[A, B](fa: DBIO[A])(f: A => DBIO[B]): DBIO[B] = fa.flatMap(f)

        override def pure[A](x: A): DBIO[A] = DBIO.successful(x)
      }

  }

}
