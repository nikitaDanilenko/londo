package services.email

import cats.effect.IO

import javax.inject.Inject

class EmailService @Inject() () {

  // TODO: Add proper implementation instead of place holder
  def sendEmail(emailParameters: EmailParameters): IO[Unit] =
    IO {
      pprint.log(emailParameters)
    }

}
