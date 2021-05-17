package errors

import cats.data.NonEmptyList

case class BulkServerException(errors: NonEmptyList[ServerError]) extends Throwable
