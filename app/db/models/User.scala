package db.models

case class User(
    id: java.util.UUID,
    nickname: String,
    email: String,
    passwordSalt: String,
    passwordHash: String,
    iterations: Int
)
