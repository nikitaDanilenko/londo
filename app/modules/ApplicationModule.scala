package modules

import db.DbConnection
import play.api.inject.Binding
import play.api.{ Configuration, Environment }

class ApplicationModule extends play.api.inject.Module {

  override def bindings(environment: Environment, configuration: Configuration): collection.Seq[Binding[_]] = {
    Seq(
      bind[DbConnection].toInstance(configuration.get[DbConnection]("db.default"))
    )
  }

}
