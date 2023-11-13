module Types.Dashboard.Buckets exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.Buckets
import Types.Dashboard.CountingBucket


type alias Buckets =
    { total : List Types.Dashboard.CountingBucket.CountingBucket
    , counting : List Types.Dashboard.CountingBucket.CountingBucket
    }


selection : SelectionSet Buckets LondoGQL.Object.Buckets
selection =
    SelectionSet.map2 Buckets
        (LondoGQL.Object.Buckets.total Types.Dashboard.CountingBucket.selection)
        (LondoGQL.Object.Buckets.counting Types.Dashboard.CountingBucket.selection)
