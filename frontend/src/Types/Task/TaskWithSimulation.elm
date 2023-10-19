module Types.Task.TaskWithSimulation exposing (..)

import Graphql.Http
import Graphql.OptionalArgument
import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Id
import Types.Simulation.Update
import Types.Task.Id
import Types.Task.Resolved
import Types.Task.Update
import Util.HttpUtil as HttpUtil


type alias ClientInput =
    { taskUpdate : Types.Task.Update.ClientInput
    , simulation : Types.Simulation.Update.ClientInput
    }


lenses :
    { taskUpdate : Lens ClientInput Types.Task.Update.ClientInput
    , simulation : Lens ClientInput Types.Simulation.Update.ClientInput
    }
lenses =
    { taskUpdate = Lens .taskUpdate (\b a -> { a | taskUpdate = b })
    , simulation = Lens .simulation (\b a -> { a | simulation = b })
    }


from : Types.Task.Resolved.Resolved -> ClientInput
from resolved =
    { taskUpdate = resolved |> .task |> Types.Task.Update.from
    , simulation = Types.Simulation.Update.from resolved.simulation
    }


toGraphQLInput : Types.Dashboard.Id.Id -> Types.Task.Id.Id -> ClientInput -> LondoGQL.InputObject.UpdateTaskWithSimulationInput
toGraphQLInput dashboardId taskId clientInput =
    { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
    , taskId = taskId |> Types.Task.Id.toGraphQLInput
    , taskUpdate = clientInput.taskUpdate |> Types.Task.Update.toGraphQLInput
    , simulationUpdate =
        clientInput.simulation
            |> Types.Simulation.Update.toGraphQLInput
            |> Graphql.OptionalArgument.fromMaybe
    }


updateWith :
    (HttpUtil.GraphQLResult Types.Task.Resolved.Resolved -> msg)
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> Types.Task.Id.Id
    -> ClientInput
    -> Cmd msg
updateWith expect authorizedAccess dashboardId taskId update =
    LondoGQL.Mutation.updateTaskWithSimulation
        { input =
            { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
            , taskId = taskId |> Types.Task.Id.toGraphQLInput
            , taskUpdate = update.taskUpdate |> Types.Task.Update.toGraphQLInput
            , simulationUpdate =
                update.simulation
                    |> Types.Simulation.Update.toGraphQLInput
                    |> Graphql.OptionalArgument.fromMaybe
            }
        }
        Types.Task.Resolved.selection
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }
