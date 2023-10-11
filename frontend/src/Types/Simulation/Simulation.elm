module Types.Simulation.Simulation exposing (..)

import Graphql.Http
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Mutation
import LondoGQL.Object
import LondoGQL.Object.Simulation
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Id
import Types.Task.Id
import Util.HttpUtil as HttpUtil


type alias Simulation =
    { reachedModifier : Int
    }


selection : SelectionSet Simulation LondoGQL.Object.Simulation
selection =
    SelectionSet.map Simulation
        LondoGQL.Object.Simulation.reachedModifier


deleteWith :
    (Types.Dashboard.Id.Id -> Types.Task.Id.Id -> HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Types.Task.Id.Id
    -> Cmd msg
deleteWith expect authorizedAccess dashboardId taskId =
    LondoGQL.Mutation.deleteSimulation
        { input =
            { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
            , taskId = taskId |> Types.Task.Id.toGraphQLInput
            }
        }
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect dashboardId taskId
            }
