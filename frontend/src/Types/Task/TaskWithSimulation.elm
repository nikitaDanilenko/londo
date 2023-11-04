module Types.Task.TaskWithSimulation exposing (..)

import Graphql.Http
import Graphql.OptionalArgument
import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Id
import Types.Simulation.Update
import Types.Task.Analysis
import Types.Task.Id
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


from : Types.Task.Analysis.Analysis -> ClientInput
from resolved =
    { taskUpdate = resolved |> .task |> Types.Task.Update.from
    , simulation =
        Types.Simulation.Update.from
            { progress = resolved.task.progress
            , simulation = resolved.simulation
            }
    }


toGraphQLInput : Types.Dashboard.Id.Id -> Types.Task.Id.Id -> ClientInput -> LondoGQL.InputObject.UpdateTaskWithSimulationInput
toGraphQLInput dashboardId taskId clientInput =
    { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
    , taskId = taskId |> Types.Task.Id.toGraphQLInput
    , taskUpdate = clientInput.taskUpdate |> Types.Task.Update.toGraphQLInput
    , simulation =
        clientInput.simulation
            |> Types.Simulation.Update.toGraphQLInput
            |> Graphql.OptionalArgument.fromMaybe
    }


updateWith :
    (HttpUtil.GraphQLResult Types.Task.Analysis.Analysis -> msg)
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
            , simulation =
                update.simulation
                    |> Types.Simulation.Update.toGraphQLInput
                    |> Graphql.OptionalArgument.fromMaybe
            }
        }
        Types.Task.Analysis.selection
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }
