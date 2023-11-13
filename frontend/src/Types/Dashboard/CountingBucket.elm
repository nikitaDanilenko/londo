module Types.Dashboard.CountingBucket exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Enum.Bucket
import LondoGQL.Object
import LondoGQL.Object.CountingBucket


type alias CountingBucket =
    { bucket : LondoGQL.Enum.Bucket.Bucket
    , amount : Int
    }


selection : SelectionSet CountingBucket LondoGQL.Object.CountingBucket
selection =
    SelectionSet.map2 CountingBucket
        LondoGQL.Object.CountingBucket.bucket
        LondoGQL.Object.CountingBucket.amount
