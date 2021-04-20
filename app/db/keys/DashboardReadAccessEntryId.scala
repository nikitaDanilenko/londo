package db.keys

import io.circe.generic.JsonCodec

@JsonCodec
case class DashboardReadAccessEntryId(
    dashboardReadAccessId: DashboardReadAccessId,
    userId: UserId
)
