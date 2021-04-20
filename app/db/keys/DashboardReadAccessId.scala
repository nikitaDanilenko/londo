package db.keys

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class DashboardReadAccessId(uuid: UUID) extends AnyVal
