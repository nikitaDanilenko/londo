package services.email

import cats.data.EitherT
import errors.{ ErrorContext, ServerError }
import play.api.libs.mailer.{ Email, MailerClient }
import cats.syntax.functor._

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

class EmailService @Inject() (
    mailerClient: MailerClient,
    mailConfig: MailConfiguration
)(implicit
    executionContext: ExecutionContext
) {

  def sendEmail(emailParameters: EmailParameters): Future[ServerError.Or[Unit]] = {
    val email = Email(
      subject = emailParameters.subject,
      from = mailConfig.from,
      to = emailParameters.to,
      cc = emailParameters.cc,
      bodyText = Some(emailParameters.message)
    )

    lazy val error = Left(ErrorContext.Mail.SendingFailed.asServerError)

    EitherT(Future(Try(mailerClient.send(email)).toEither)).void
      .fold(_ => error, Right(_))
      .recover { case _ =>
        error
      }
  }

}
