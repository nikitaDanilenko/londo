package services.email

import play.api.libs.mailer.{ Email, MailerClient }

import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try

class EmailService @Inject() (
    mailerClient: MailerClient
)(implicit
    executionContext: ExecutionContext
) {

  private val mailConfig = MailConfiguration.default

  def sendEmail(emailParameters: EmailParameters): Future[Unit] = {
    val email = Email(
      subject = emailParameters.subject,
      from = mailConfig.from,
      to = emailParameters.to,
      cc = emailParameters.cc,
      bodyText = Some(emailParameters.message)
    )
    Future {
      Try(mailerClient.send(email))
    }.flatMap(_.fold(Future.failed, _ => Future.unit))
  }

}
