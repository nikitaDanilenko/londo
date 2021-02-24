package db

import com.typesafe.config.Config
import play.api.ConfigLoader

sealed trait DbConnection {
  def driver: String
  def url: String
  def userName: String
  def password: String
}

object DbConnection {

  private case class DbConnectionImpl(
      override val driver: String,
      override val url: String,
      override val userName: String,
      override val password: String
  ) extends DbConnection

  def apply(config: Config): DbConnection =
    DbConnectionImpl(
      driver = config.getString("driver"),
      url = config.getString("url"),
      userName = config.getString("username"),
      password = config.getString("password")
    )

  implicit val dbConnectionConfigLoader: ConfigLoader[DbConnection] =
    ConfigLoader(c => p => DbConnection(c.getConfig(p)))

}
