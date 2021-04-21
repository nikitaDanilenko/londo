package db.keys

import io.circe.generic.JsonCodec

import java.util.UUID

@JsonCodec
case class ProjectId(uuid: UUID) extends AnyVal {
  def asProjectReadAccessId: ProjectReadAccessId = ProjectReadAccessId(uuid)
  def asProjectWriteAccessId: ProjectWriteAccessId = ProjectWriteAccessId(uuid)
}
