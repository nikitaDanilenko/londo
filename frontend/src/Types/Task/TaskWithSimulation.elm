module Types.Task.TaskWithSimulation exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject
import LondoGQL.Mutation
import Math.Positive
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


type alias ArgumentComplement =
    { dashboardId : Types.Dashboard.Id.Id
    , taskId : Types.Task.Id.Id
    , numberOfTotalTasks : Maybe Math.Positive.Positive
    , numberOfCountingTasks : Maybe Math.Positive.Positive
    , numberOfDecimalPlaces : Math.Positive.Positive
    }


toGraphQLInput :
    ArgumentComplement
    -> ClientInput
    -> LondoGQL.InputObject.UpdateTaskWithSimulationInput
toGraphQLInput complement clientInput =
    { dashboardId = complement |> .dashboardId |> Types.Dashboard.Id.toGraphQLInput
    , taskId = complement |> .taskId |> Types.Task.Id.toGraphQLInput
    , taskUpdate = clientInput.taskUpdate |> Types.Task.Update.toGraphQLInput
    , simulation =
        clientInput.simulation
            |> Types.Simulation.Update.toGraphQLInput
            |> OptionalArgument.fromMaybe
    , numberOfTotalTasks =
        complement.numberOfTotalTasks
            |> Maybe.map Math.Positive.toGraphQLInput
            |> OptionalArgument.fromMaybe
    , numberOfCountingTasks =
        complement.numberOfCountingTasks
            |> Maybe.map Math.Positive.toGraphQLInput
            |> OptionalArgument.fromMaybe
    , numberOfDecimalPlaces =
        complement.numberOfDecimalPlaces
            |> Math.Positive.toGraphQLInput
    }


updateWith :
    (HttpUtil.GraphQLResult Types.Task.Analysis.Analysis -> msg)
    -> AuthorizedAccess
    -> ArgumentComplement
    -> ClientInput
    -> Cmd msg
updateWith expect authorizedAccess complement update =
    LondoGQL.Mutation.updateTaskWithSimulation
        { input =
            { dashboardId = complement |> .dashboardId |> Types.Dashboard.Id.toGraphQLInput
            , taskId = complement |> .taskId |> Types.Task.Id.toGraphQLInput
            , taskUpdate = update.taskUpdate |> Types.Task.Update.toGraphQLInput
            , simulation =
                update.simulation
                    |> Types.Simulation.Update.toGraphQLInput
                    |> OptionalArgument.fromMaybe
            , numberOfTotalTasks =
                complement.numberOfTotalTasks
                    |> Maybe.map Math.Positive.toGraphQLInput
                    |> OptionalArgument.fromMaybe
            , numberOfCountingTasks =
                complement.numberOfCountingTasks
                    |> Maybe.map Math.Positive.toGraphQLInput
                    |> OptionalArgument.fromMaybe
            , numberOfDecimalPlaces =
                complement.numberOfDecimalPlaces
                    |> Math.Positive.toGraphQLInput
            }
        }
        Types.Task.Analysis.selection
        |> HttpUtil.mutationWith expect authorizedAccess
