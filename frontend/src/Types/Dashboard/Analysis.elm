module Types.Dashboard.Analysis exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.DashboardAnalysis
import LondoGQL.Query
import Math.Positive
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Types.Dashboard.Statistics
import Types.Project.Analysis
import Util.HttpUtil as HttpUtil


type alias Analysis =
    { dashboard : Types.Dashboard.Dashboard.Dashboard
    , projectAnalyses : List Types.Project.Analysis.Analysis
    , dashboardStatistics : Types.Dashboard.Statistics.Statistics
    }


selection : SelectionSet Analysis LondoGQL.Object.DashboardAnalysis
selection =
    SelectionSet.map3
        Analysis
        (LondoGQL.Object.DashboardAnalysis.dashboard Types.Dashboard.Dashboard.selection)
        (LondoGQL.Object.DashboardAnalysis.projectAnalyses Types.Project.Analysis.selection)
        statisticsSelection


statisticsSelection : SelectionSet Types.Dashboard.Statistics.Statistics LondoGQL.Object.DashboardAnalysis
statisticsSelection =
    LondoGQL.Object.DashboardAnalysis.dashboardStatistics Types.Dashboard.Statistics.selection


fetchWith :
    (HttpUtil.GraphQLResult Analysis -> msg)
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Math.Positive.Positive
    -> Cmd msg
fetchWith expect =
    fetchWithSelection
        { expect = expect
        , selection = selection
        }


fetchWithSelection :
    { expect : HttpUtil.GraphQLResult a -> msg
    , selection : SelectionSet a LondoGQL.Object.DashboardAnalysis
    }
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Math.Positive.Positive
    -> Cmd msg
fetchWithSelection ps authorizedAccess dashboardId numberOfDecimalPlaces =
    LondoGQL.Query.fetchDashboardAnalysis
        { input =
            { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
            , numberOfDecimalPlaces = numberOfDecimalPlaces |> Math.Positive.toGraphQLInput
            }
        }
        ps.selection
        |> HttpUtil.queryWith
            ps.expect
            authorizedAccess
