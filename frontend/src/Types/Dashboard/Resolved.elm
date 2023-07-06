module Types.Dashboard.Resolved exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.ResolvedDashboard
import LondoGQL.Query
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Types.DashboardEntry.Entry
import Util.HttpUtil as HttpUtil


type alias Resolved =
    { dashboard : Types.Dashboard.Dashboard.Dashboard
    , entries : List Types.DashboardEntry.Entry.Entry
    }


selection : SelectionSet Resolved LondoGQL.Object.ResolvedDashboard
selection =
    SelectionSet.map2
        Resolved
        (LondoGQL.Object.ResolvedDashboard.dashboard Types.Dashboard.Dashboard.selection)
        (LondoGQL.Object.ResolvedDashboard.entries Types.DashboardEntry.Entry.selection)


fetchWith :
    (HttpUtil.GraphQLResult Resolved -> msg)
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Cmd msg
fetchWith expect authorizedAccess dashboardId =
    LondoGQL.Query.fetchResolvedDashboard
        { input =
            { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
            }
        }
        selection
        |> HttpUtil.queryWith
            expect
            authorizedAccess
