package db.keys

import io.circe.generic.JsonCodec

@JsonCodec
case class DashboardWriteAccessEntryId(
    dashboardWriteAccessId: DashboardWriteAccessId,
    userId: UserId
)
