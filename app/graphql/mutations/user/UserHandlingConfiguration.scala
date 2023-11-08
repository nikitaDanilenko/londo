package graphql.mutations.user

import graphql.mutations.user.inputs.UserIdentifier
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._
import pureconfig.{ CamelCase, ConfigFieldMapping, ConfigSource }
import services.email.EmailParameters

case class UserHandlingConfiguration(
    restrictedDurationInSeconds: Int,
    subject: Subject,
    greeting: String,
    registrationMessage: String,
    recoveryMessage: String,
    deletionMessage: String,
    closing: String,
    frontend: String
)

object UserHandlingConfiguration {
  implicit def hint[A]: ProductHint[A] = ProductHint[A](ConfigFieldMapping(CamelCase, CamelCase))

  val default: UserHandlingConfiguration = ConfigSource.default
    .at("userHandlingConfiguration")
    .loadOrThrow[UserHandlingConfiguration]

  def registrationEmail(
      userConfiguration: UserHandlingConfiguration,
      userIdentifier: UserIdentifier,
      jwt: String
  ): EmailParameters =
    emailWith(
      userConfiguration = userConfiguration,
      operation = Operation.Registration,
      userIdentifier = userIdentifier,
      jwt = jwt
    )

  def recoveryEmail(
      userConfiguration: UserHandlingConfiguration,
      userIdentifier: UserIdentifier,
      jwt: String
  ): EmailParameters =
    emailWith(
      userConfiguration = userConfiguration,
      operation = Operation.Recovery,
      userIdentifier = userIdentifier,
      jwt = jwt
    )

  def deletionEmail(
      userConfiguration: UserHandlingConfiguration,
      userIdentifier: UserIdentifier,
      jwt: String
  ): EmailParameters =
    emailWith(
      userConfiguration = userConfiguration,
      operation = Operation.Deletion,
      userIdentifier = userIdentifier,
      jwt = jwt
    )

  private sealed trait Operation

  private object Operation {
    case object Registration extends Operation
    case object Recovery     extends Operation
    case object Deletion     extends Operation
  }

  private case class AddressWithMessage(
      suffix: String,
      message: String
  )

  private def emailComponents(userConfiguration: UserHandlingConfiguration): Map[Operation, AddressWithMessage] =
    Map(
      Operation.Registration -> AddressWithMessage("confirm-registration", userConfiguration.registrationMessage),
      Operation.Recovery     -> AddressWithMessage("recover-account", userConfiguration.recoveryMessage),
      Operation.Deletion     -> AddressWithMessage("delete-account", userConfiguration.deletionMessage)
    )

  private def subjectBy(subject: Subject, operation: Operation): String =
    operation match {
      case Operation.Registration => subject.registration
      case Operation.Recovery     => subject.recovery
      case Operation.Deletion     => subject.deletion
    }

  private def emailWith(
      userConfiguration: UserHandlingConfiguration,
      operation: Operation,
      userIdentifier: UserIdentifier,
      jwt: String
  ): EmailParameters = {
    val addressWithMessage = emailComponents(userConfiguration)(operation)
    val message =
      s"""${userConfiguration.greeting} ${userIdentifier.nickname},
           |
           |${addressWithMessage.message}
           |
           |${userConfiguration.frontend}/#/${addressWithMessage.suffix}/nickname/${userIdentifier.nickname}/email/${userIdentifier.email}/token/$jwt
           |
           |${userConfiguration.closing}""".stripMargin

    EmailParameters(
      to = Seq(userIdentifier.email),
      cc = Seq.empty,
      subject = subjectBy(userConfiguration.subject, operation),
      message = message
    )

  }

}
