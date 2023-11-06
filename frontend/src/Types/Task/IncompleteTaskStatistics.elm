module Types.Task.IncompleteTaskStatistics exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.IncompleteTaskStatistics
import LondoGQL.Scalar
import Types.Task.After


type alias IncompleteTaskStatistics =
    { mean : LondoGQL.Scalar.BigDecimal
    , total : Types.Task.After.After
    , counted : Types.Task.After.After
    }


selection : SelectionSet IncompleteTaskStatistics LondoGQL.Object.IncompleteTaskStatistics
selection =
    SelectionSet.map3
        IncompleteTaskStatistics
        LondoGQL.Object.IncompleteTaskStatistics.mean
        (LondoGQL.Object.IncompleteTaskStatistics.total Types.Task.After.selection)
        (LondoGQL.Object.IncompleteTaskStatistics.counted Types.Task.After.selection)
