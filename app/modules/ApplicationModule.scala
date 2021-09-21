package modules

import db.DbConnection
import play.api.inject.Binding
import play.api.{ Configuration, Environment }
import security.SignatureConfiguration
import security.jwt.JwtConfiguration
import services.user.FrontendConfiguration

class ApplicationModule extends play.api.inject.Module {

  override def bindings(environment: Environment, configuration: Configuration): collection.Seq[Binding[_]] = {
    val settings = Seq(
      bind[DbConnection].toInstance(configuration.get[DbConnection]("db.default"))
    )

    val configurations = Seq(
      bind[JwtConfiguration].toInstance(JwtConfiguration(configuration)),
      bind[SignatureConfiguration].toInstance(SignatureConfiguration(configuration)),
      bind[FrontendConfiguration].toInstance(FrontendConfiguration(configuration))
    )

    List(settings, configurations).flatten
  }

}
