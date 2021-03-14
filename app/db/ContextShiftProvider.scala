package db

import cats.effect.{ ContextShift, IO }

import scala.concurrent.ExecutionContext

object ContextShiftProvider {

  def fromExecutionContext(implicit executionContext: ExecutionContext): ContextShift[IO] =
    IO.contextShift(executionContext)

}
