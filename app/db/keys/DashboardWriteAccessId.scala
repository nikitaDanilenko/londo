package db.keys

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class DashboardWriteAccessId(uuid: UUID) extends AnyVal
