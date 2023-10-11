module Types.Task.Resolved exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.ResolvedTask
import Types.Simulation.Simulation exposing (Simulation)
import Types.Task.Task exposing (Task)


type alias Resolved =
    { task : Task
    , simulation : Maybe Simulation
    }


selection : SelectionSet Resolved LondoGQL.Object.ResolvedTask
selection =
    SelectionSet.map2 Resolved
        (LondoGQL.Object.ResolvedTask.task Types.Task.Task.selection)
        (LondoGQL.Object.ResolvedTask.simulation Types.Simulation.Simulation.selection)
