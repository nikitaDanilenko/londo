module Types.Dashboard.WithoutSimulation exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.WithoutSimulation
import Math.Natural


type alias WithoutSimulation =
    { total : Math.Natural.Natural
    , counting : Math.Natural.Natural
    }


selection : SelectionSet WithoutSimulation LondoGQL.Object.WithoutSimulation
selection =
    SelectionSet.map2 WithoutSimulation
        (LondoGQL.Object.WithoutSimulation.total Math.Natural.selection)
        (LondoGQL.Object.WithoutSimulation.counting Math.Natural.selection)
