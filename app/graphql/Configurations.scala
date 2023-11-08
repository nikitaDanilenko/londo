package graphql

import graphql.mutations.user.UserHandlingConfiguration
import security.jwt.JwtConfiguration
import services.email.MailConfiguration

import javax.inject.Inject

case class Configurations @Inject() (
    userHandlingConfiguration: UserHandlingConfiguration,
    jwtConfiguration: JwtConfiguration,
    mailConfiguration: MailConfiguration
)
