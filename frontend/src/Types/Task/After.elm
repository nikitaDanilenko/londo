module Types.Task.After exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.After
import LondoGQL.Scalar


type alias After =
    { one : LondoGQL.Scalar.BigDecimal
    , completion : LondoGQL.Scalar.BigDecimal
    , simulation : Maybe LondoGQL.Scalar.BigDecimal
    }


selection : SelectionSet After LondoGQL.Object.After
selection =
    SelectionSet.map3 After
        LondoGQL.Object.After.one
        LondoGQL.Object.After.completion
        LondoGQL.Object.After.simulation
