module Types.Project.DeeplyResolved exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.DeeplyResolvedProject
import Types.Project.Project
import Types.Task.Resolved


type alias DeeplyResolved =
    { project : Types.Project.Project.Project
    , tasks : List Types.Task.Resolved.Resolved
    }


selection : SelectionSet DeeplyResolved LondoGQL.Object.DeeplyResolvedProject
selection =
    SelectionSet.map2 DeeplyResolved
        (LondoGQL.Object.DeeplyResolvedProject.project Types.Project.Project.selection)
        (LondoGQL.Object.DeeplyResolvedProject.tasks Types.Task.Resolved.selection)
