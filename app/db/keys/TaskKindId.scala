package db.keys

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class TaskKindId(uuid: UUID) extends AnyVal