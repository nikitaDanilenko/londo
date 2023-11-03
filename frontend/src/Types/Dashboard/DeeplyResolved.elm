module Types.Dashboard.DeeplyResolved exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.DeeplyResolvedDashboard
import LondoGQL.Query
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Types.Dashboard.Statistics
import Types.Project.DeeplyResolved
import Util.HttpUtil as HttpUtil


type alias DeeplyResolved =
    { dashboard : Types.Dashboard.Dashboard.Dashboard
    , resolvedProjects : List Types.Project.DeeplyResolved.DeeplyResolved
    , dashboardStatistics : Types.Dashboard.Statistics.Statistics
    }


selection : SelectionSet DeeplyResolved LondoGQL.Object.DeeplyResolvedDashboard
selection =
    SelectionSet.map3
        DeeplyResolved
        (LondoGQL.Object.DeeplyResolvedDashboard.dashboard Types.Dashboard.Dashboard.selection)
        (LondoGQL.Object.DeeplyResolvedDashboard.resolvedProjects Types.Project.DeeplyResolved.selection)
        statisticsSelection


statisticsSelection : SelectionSet Types.Dashboard.Statistics.Statistics LondoGQL.Object.DeeplyResolvedDashboard
statisticsSelection =
    LondoGQL.Object.DeeplyResolvedDashboard.dashboardStatistics Types.Dashboard.Statistics.selection


fetchWith :
    (HttpUtil.GraphQLResult DeeplyResolved -> msg)
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Cmd msg
fetchWith expect =
    fetchWithSelection
        { expect = expect
        , selection = selection
        }


fetchWithSelection :
    { expect : HttpUtil.GraphQLResult a -> msg
    , selection : SelectionSet a LondoGQL.Object.DeeplyResolvedDashboard
    }
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Cmd msg
fetchWithSelection ps authorizedAccess dashboardId =
    LondoGQL.Query.fetchDeeplyResolvedDashboard
        { input =
            { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
            }
        }
        ps.selection
        |> HttpUtil.queryWith
            ps.expect
            authorizedAccess
