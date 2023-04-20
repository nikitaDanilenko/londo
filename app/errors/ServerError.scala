package errors

import cats.data.{ NonEmptyList, Validated, ValidatedNel }
import io.circe.syntax._
import io.circe.{ Encoder, Json }

sealed trait ServerError {
  def message: String
}

object ServerError {

  type Or[A]    = Either[ServerError, A]
  type Valid[A] = ValidatedNel[ServerError, A]

  implicit val serverErrorEncoder: Encoder[ServerError] = Encoder.instance[ServerError] { serverError =>
    Json.obj(
      "message" -> serverError.message.asJson
    )
  }

  def fromContext(errorContext: ErrorContext): ServerError =
    new ServerError {
      override val message: String = errorContext.message
    }

  def fromEither[A](either: Or[A]): Valid[A] =
    Validated.fromEither[NonEmptyList[ServerError], A](either.left.map(NonEmptyList.of(_)))

  def fromOption[A](option: Option[A], errorCase: => ServerError): ServerError.Or[A] =
    option.toRight(errorCase)

  def result[A](a: A): ServerError.Or[A] = Right(a)

}
