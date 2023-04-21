package services.user

import db.UserId
import db.generated.Tables
import io.scalaland.chimney.Transformer
import utils.transformer.implicits._

case class User(
    id: UserId,
    nickname: String,
    displayName: Option[String],
    description: Option[String],
    email: String,
    salt: String,
    hash: String
)

object User {

  implicit val fromDB: Transformer[Tables.UserRow, User] =
    Transformer
      .define[Tables.UserRow, User]
      .buildTransformer

  implicit val toDB: Transformer[User, Tables.UserRow] =
    Transformer
      .define[User, Tables.UserRow]
      .buildTransformer

}
