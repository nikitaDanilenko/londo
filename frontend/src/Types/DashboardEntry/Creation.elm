module Types.DashboardEntry.Creation exposing (..)

import Graphql.Http
import LondoGQL.InputObject
import LondoGQL.Mutation
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Id
import Types.DashboardEntry.Entry
import Types.Project.Id
import Util.HttpUtil as HttpUtil


type alias ClientInput =
    { dashboardId : Types.Dashboard.Id.Id
    , projectId : Types.Project.Id.Id
    }


default : Types.Dashboard.Id.Id -> Types.Project.Id.Id -> ClientInput
default =
    ClientInput


toGraphQLInput : ClientInput -> LondoGQL.InputObject.DashboardEntryCreation
toGraphQLInput clientInput =
    { projectId = clientInput.projectId |> Types.Project.Id.toGraphQLInput
    }


createWith :
    (HttpUtil.GraphQLResult Types.DashboardEntry.Entry.Entry -> msg)
    -> AuthorizedAccess
    -> ClientInput
    -> Cmd msg
createWith expect authorizedAccess creation =
    LondoGQL.Mutation.createDashboardEntry
        { input =
            { dashboardId = creation |> .dashboardId |> Types.Dashboard.Id.toGraphQLInput
            , dashboardEntryCreation = creation |> toGraphQLInput
            }
        }
        Types.DashboardEntry.Entry.selection
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }
