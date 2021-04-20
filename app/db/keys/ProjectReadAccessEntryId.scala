package db.keys

import io.circe.generic.JsonCodec

@JsonCodec
case class ProjectReadAccessEntryId(
    projectReadAccessId: ProjectReadAccessId,
    userId: UserId
)
