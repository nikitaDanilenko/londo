module Types.Task.TaskWithSimulation exposing (..)

import Graphql.OptionalArgument
import LondoGQL.InputObject
import Monocle.Lens exposing (Lens)
import Types.Dashboard.Id
import Types.Simulation.Update
import Types.Task.Id
import Types.Task.Task
import Types.Task.Update


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


from : Types.Task.Task.Task -> ClientInput
from task =
    { taskUpdate = task |> Types.Task.Update.from
    , simulation = Types.Simulation.Update.initial
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



--updateWith :
--    (HttpUtil.GraphQLResult Types.Task.Task.Task -> msg)
--    -> AuthorizedAccess
--    -> Types.Dashboard.Id.Id
--    -> Types.Task.Id.Id
--    -> ClientInput
--    -> Cmd msg
--updateWith expect authorizedAccess dashboardId taskId update =
--    LondoGQL.Mutation.updateTaskWithSimulation
--    {
--    input = {
--
--    }
--    }
