package utils.fp

object FunctionUtil {

  object syntax {

    implicit class PipeSyntax[A](val a: A) extends AnyVal {
      def |>[B](f: A => B): B = f(a)
    }

  }

}
