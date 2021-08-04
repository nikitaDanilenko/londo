package errors

import cats.data.{ EitherT, NonEmptyList, Validated, ValidatedNel }
import doobie.ConnectionIO
import io.circe.{ Encoder, Json }
import io.circe.syntax._

sealed trait ServerError {
  def message: String
}

object ServerError {

  type Or[A] = Either[ServerError, A]
  type Valid[A] = ValidatedNel[ServerError, A]

  implicit val serverErrorEncoder: Encoder[ServerError] = Encoder.instance[ServerError] { serverError =>
    Json.obj(
      "message" -> serverError.message.asJson
    )
  }

  // TODO: Check necessity
  case class BulkError(serverErrors: NonEmptyList[ServerError]) extends ServerError {

    override lazy val message: String = {
      val bulkMessage = serverErrors.map(serverError => s"* ${serverError.message}").toList.mkString("\n")
      s"The following errors occurred:\n$bulkMessage"
    }

  }

  def fromContext(errorContext: ErrorContext): ServerError =
    new ServerError {
      override val message: String = errorContext.message
    }

  def fromEmpty[A](option: Option[A], ifNonEmpty: => ServerError): Valid[Unit] =
    Validated.fromEither {
      option match {
        case Some(_) => Left(NonEmptyList.of(ifNonEmpty))
        case None    => Right(())
      }
    }

  def fromEither[A](either: Or[A]): Valid[A] =
    Validated.fromEither[NonEmptyList[ServerError], A](either.left.map(NonEmptyList.of(_)))

  def fromEitherNel[A](either: Either[NonEmptyList[ServerError], A]): Valid[A] =
    Validated.fromEither(either)

  def fromCondition[A](condition: Boolean, errorCase: => ServerError, successCase: => A): Valid[A] =
    Validated.condNel(condition, successCase, errorCase)

  def fromOption[A](option: Option[A], errorCase: => ServerError): ServerError.Or[A] =
    option.toRight(errorCase)

  def result[A](a: A): ServerError.Or[A] = Right(a)

  def fromValidated[A](v: ServerError.Valid[A]): ServerError.Or[A] =
    v.toEither.left.map(BulkError)

  def valid[A](a: A): Valid[A] =
    Validated.valid(a)

  def liftNelC[A](ca: ConnectionIO[A]): EitherT[ConnectionIO, NonEmptyList[ServerError], A] =
    EitherT.liftF[ConnectionIO, NonEmptyList[ServerError], A](ca)

  def liftC[A](ca: ConnectionIO[A]): EitherT[ConnectionIO, ServerError, A] =
    EitherT.liftF[ConnectionIO, ServerError, A](ca)

}
