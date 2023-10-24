module Types.DashboardEntry.Id exposing (..)

import Types.Dashboard.Id
import Types.Project.Id


type Id
    = Id
        { dashboardId : Types.Dashboard.Id.Id
        , projectId : Types.Project.Id.Id
        }


unwrap :
    Id
    ->
        { dashboardId : Types.Dashboard.Id.Id
        , projectId : Types.Project.Id.Id
        }
unwrap (Id id) =
    id
