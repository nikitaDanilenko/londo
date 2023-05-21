package services.email

import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import pureconfig.{ CamelCase, ConfigFieldMapping, ConfigSource }

case class MailConfiguration(
    host: String,
    tls: String,
    from: String,
    port: Int,
    user: Option[String],
    password: Option[String]
)

object MailConfiguration {
  implicit def hint[A]: ProductHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))

  val default: MailConfiguration = ConfigSource.default
    .at("play.mailer")
    .loadOrThrow[MailConfiguration]

}
