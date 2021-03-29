package modules

import db.DbConnection
import play.api.inject.Binding
import play.api.{ Configuration, Environment }
import utils.jwt.JwtConfiguration

class ApplicationModule extends play.api.inject.Module {

  override def bindings(environment: Environment, configuration: Configuration): collection.Seq[Binding[_]] = {
    val settings = Seq(
      bind[DbConnection].toInstance(configuration.get[DbConnection]("db.default"))
    )

    val configurations = Seq(
      bind[JwtConfiguration].toInstance(JwtConfiguration(configuration))
    )

    List(settings, configurations).flatten
  }

}
