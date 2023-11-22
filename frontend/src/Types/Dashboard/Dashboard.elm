module Types.Dashboard.Dashboard exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Enum.Visibility
import LondoGQL.Mutation
import LondoGQL.Object
import LondoGQL.Object.Dashboard
import LondoGQL.Query
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Id
import Util.HttpUtil as HttpUtil


type alias Dashboard =
    { id : Types.Dashboard.Id.Id
    , header : String
    , description : Maybe String
    , visibility : LondoGQL.Enum.Visibility.Visibility
    }


selection : SelectionSet Dashboard LondoGQL.Object.Dashboard
selection =
    SelectionSet.map4 Dashboard
        (LondoGQL.Object.Dashboard.id Types.Dashboard.Id.selection)
        LondoGQL.Object.Dashboard.header
        LondoGQL.Object.Dashboard.description
        LondoGQL.Object.Dashboard.visibility


fetchAllWith :
    (HttpUtil.GraphQLResult (List Dashboard) -> msg)
    -> AuthorizedAccess
    -> Cmd msg
fetchAllWith expect authorizedAccess =
    LondoGQL.Query.fetchAllDashboards
        selection
        |> HttpUtil.queryWith expect authorizedAccess


deleteWith :
    (Types.Dashboard.Id.Id -> HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Cmd msg
deleteWith expect authorizedAccess dashboardId =
    LondoGQL.Mutation.deleteDashboard
        { input =
            { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
            }
        }
        |> HttpUtil.mutationWith (expect dashboardId) authorizedAccess
