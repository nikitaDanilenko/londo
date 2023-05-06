module Math.Natural exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Natural
import Maybe.Extra


type alias Natural =
    { nonNegative : Int }


toString : Natural -> String
toString =
    .nonNegative >> String.fromInt


fromString : String -> Maybe Natural
fromString s =
    String.toInt s
        |> Maybe.Extra.filter (\x -> x >= 0)
        |> Maybe.map (\n -> { nonNegative = n })


zero : Natural
zero =
    { nonNegative = 0 }


min : Natural -> Natural -> Natural
min x y =
    if x.nonNegative <= y.nonNegative then
        x

    else
        y


toGraphQLInput : Natural -> LondoGQL.InputObject.NaturalInput
toGraphQLInput =
    identity


selection : SelectionSet Natural LondoGQL.Object.Natural
selection =
    SelectionSet.map Natural LondoGQL.Object.Natural.nonNegative
