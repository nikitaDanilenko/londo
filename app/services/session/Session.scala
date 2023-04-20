package services.session

import db.SessionId

import java.time.LocalDateTime

case class Session(
    id: SessionId,
    createdAt: LocalDateTime
)
