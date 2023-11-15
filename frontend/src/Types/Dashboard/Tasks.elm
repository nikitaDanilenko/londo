module Types.Dashboard.Tasks exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.Tasks


type alias Tasks =
    { total : Int
    , counting : Int
    }


selection : SelectionSet Tasks LondoGQL.Object.Tasks
selection =
    SelectionSet.map2 Tasks
        LondoGQL.Object.Tasks.total
        LondoGQL.Object.Tasks.counting
