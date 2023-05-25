module Types.DashboardEntry.Entry exposing (..)

import Graphql.Http
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Mutation
import LondoGQL.Object
import LondoGQL.Object.DashboardEntry
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Id
import Types.Project.Id
import Util.HttpUtil as HttpUtil


type alias DashboardEntry =
    { projectId : Types.Project.Id.Id
    }


selection : SelectionSet DashboardEntry LondoGQL.Object.DashboardEntry
selection =
    SelectionSet.map
        DashboardEntry
        (LondoGQL.Object.DashboardEntry.projectId Types.Project.Id.selection)


deleteWith :
    (Types.Dashboard.Id.Id -> Types.Project.Id.Id -> HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Types.Project.Id.Id
    -> Cmd msg
deleteWith expect authorizedAccess dashboardId projectId =
    LondoGQL.Mutation.deleteDashboardEntry
        { input =
            { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
            , projectId = projectId |> Types.Project.Id.toGraphQLInput
            }
        }
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect dashboardId projectId
            }
