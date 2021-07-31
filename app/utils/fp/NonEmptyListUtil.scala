package utils.fp

import cats.data.NonEmptyList

object NonEmptyListUtil {

  def foldLeft1[A](nonEmptyList: NonEmptyList[A])(f: (A, A) => A): A =
    nonEmptyList.tail.foldLeft(nonEmptyList.head)(f)

  def foldMapLeft[A, B](nonEmptyList: NonEmptyList[A], process: A => B)(f: (B, B) => B): B =
    nonEmptyList.tail.foldLeft(process(nonEmptyList.head)) { (b, a) => f(b, process(a)) }

}
