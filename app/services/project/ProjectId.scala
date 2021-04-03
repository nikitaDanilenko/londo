package services.project

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class ProjectId(uuid: UUID) extends AnyVal
