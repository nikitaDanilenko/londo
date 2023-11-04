module Types.Task.Analysis exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.TaskAnalysis
import Types.Simulation.Simulation exposing (Simulation)
import Types.Task.Task exposing (Task)


type alias Analysis =
    { task : Task
    , simulation : Maybe Simulation
    }


selection : SelectionSet Analysis LondoGQL.Object.TaskAnalysis
selection =
    SelectionSet.map2 Analysis
        (LondoGQL.Object.TaskAnalysis.task Types.Task.Task.selection)
        (LondoGQL.Object.TaskAnalysis.simulation Types.Simulation.Simulation.selection)
