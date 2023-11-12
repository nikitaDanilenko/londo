module Types.Project.Analysis exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.ProjectAnalysis
import Types.Project.Project
import Types.Task.Analysis


type alias Analysis =
    { project : Types.Project.Project.Project
    , tasks : List Types.Task.Analysis.Analysis
    }


selection : SelectionSet Analysis LondoGQL.Object.ProjectAnalysis
selection =
    SelectionSet.map2 Analysis
        (LondoGQL.Object.ProjectAnalysis.project Types.Project.Project.selection)
        (LondoGQL.Object.ProjectAnalysis.tasks Types.Task.Analysis.selection)
