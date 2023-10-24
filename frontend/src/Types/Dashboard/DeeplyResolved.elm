module Types.Dashboard.DeeplyResolved exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.DeeplyResolvedDashboard
import LondoGQL.Query
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Types.Project.DeeplyResolved
import Util.HttpUtil as HttpUtil


type alias DeeplyResolved =
    { dashboard : Types.Dashboard.Dashboard.Dashboard
    , resolvedProjects : List Types.Project.DeeplyResolved.DeeplyResolved
    }


selection : SelectionSet DeeplyResolved LondoGQL.Object.DeeplyResolvedDashboard
selection =
    SelectionSet.map2
        DeeplyResolved
        (LondoGQL.Object.DeeplyResolvedDashboard.dashboard Types.Dashboard.Dashboard.selection)
        (LondoGQL.Object.DeeplyResolvedDashboard.resolvedProjects Types.Project.DeeplyResolved.selection)


fetchWith :
    (HttpUtil.GraphQLResult DeeplyResolved -> msg)
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Cmd msg
fetchWith expect authorizedAccess dashboardId =
    LondoGQL.Query.fetchDeeplyResolvedDashboard
        { input =
            { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
            }
        }
        selection
        |> HttpUtil.queryWith
            expect
            authorizedAccess
