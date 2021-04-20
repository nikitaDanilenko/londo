package db.keys

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class ProjectWriteAccessId(uuid: UUID) extends AnyVal
