package services.task

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class TaskId(uuid: UUID) extends AnyVal
