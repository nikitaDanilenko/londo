package db.keys

import io.circe.generic.JsonCodec

@JsonCodec
case class ProjectWriteAccessEntryId(
    projectWriteAccessId: ProjectWriteAccessId,
    userId: UserId
)
