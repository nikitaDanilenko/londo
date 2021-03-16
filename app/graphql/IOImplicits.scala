package graphql

import cats.effect.{ Async, ContextShift, IO }
import db.ContextShiftProvider

import scala.concurrent.ExecutionContext

trait IOImplicits[F[_]] {
  implicit val asyncF: Async[F]
  implicit val contextShiftF: ContextShift[F]
}

object IOImplicits {

  def fromExecutionContext(implicit executionContext: ExecutionContext): IOImplicits[IO] = {
    val asyncIO = Async[IO]
    new IOImplicits[IO] {
      override implicit val asyncF: Async[IO] = asyncIO
      override implicit val contextShiftF: ContextShift[IO] = ContextShiftProvider.fromExecutionContext
    }
  }

}
