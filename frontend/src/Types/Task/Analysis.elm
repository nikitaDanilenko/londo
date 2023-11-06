module Types.Task.Analysis exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.TaskAnalysis
import Types.Simulation.Simulation
import Types.Task.IncompleteTaskStatistics
import Types.Task.Task


type alias Analysis =
    { task : Types.Task.Task.Task
    , simulation : Maybe Types.Simulation.Simulation.Simulation
    , incompleteTaskStatistics : Maybe Types.Task.IncompleteTaskStatistics.IncompleteTaskStatistics
    }


selection : SelectionSet Analysis LondoGQL.Object.TaskAnalysis
selection =
    SelectionSet.map3 Analysis
        (LondoGQL.Object.TaskAnalysis.task Types.Task.Task.selection)
        (LondoGQL.Object.TaskAnalysis.simulation Types.Simulation.Simulation.selection)
        (LondoGQL.Object.TaskAnalysis.incompleteStatistics Types.Task.IncompleteTaskStatistics.selection)
