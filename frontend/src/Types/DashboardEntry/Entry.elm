module Types.DashboardEntry.Entry exposing (..)

import Graphql.Http
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Mutation
import LondoGQL.Object
import LondoGQL.Object.DashboardEntry
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Id
import Types.DashboardEntry.Id
import Types.Project.Id
import Util.HttpUtil as HttpUtil


type alias Entry =
    { projectId : Types.Project.Id.Id
    }


selection : SelectionSet Entry LondoGQL.Object.DashboardEntry
selection =
    SelectionSet.map
        Entry
        (LondoGQL.Object.DashboardEntry.projectId Types.Project.Id.selection)


deleteWith :
    (Types.DashboardEntry.Id.Id -> HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> Types.DashboardEntry.Id.Id
    -> Cmd msg
deleteWith expect authorizedAccess dashboardEntryId =
    let
        unwrapped =
            Types.DashboardEntry.Id.unwrap dashboardEntryId
    in
    LondoGQL.Mutation.deleteDashboardEntry
        { input =
            { dashboardId = unwrapped |> .dashboardId |> Types.Dashboard.Id.toGraphQLInput
            , projectId = unwrapped |> .projectId |> Types.Project.Id.toGraphQLInput
            }
        }
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect dashboardEntryId
            }
