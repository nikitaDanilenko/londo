import shapeless.tag.@@

import java.util.UUID

package object db {

  sealed trait DashboardTag

  type DashboardId = UUID @@ DashboardTag

  sealed trait ProjectTag

  type ProjectId = UUID @@ ProjectTag

  sealed trait UserTag

  type UserId = UUID @@ UserTag

  sealed trait SessionTag

  type SessionId = UUID @@ SessionTag

  sealed trait PlainTaskTag

  type PlainTaskId = UUID @@ PlainTaskTag

  sealed trait ReferenceTaskTag

  type ReferenceTaskId = UUID @@ ReferenceTaskTag
}
