module Math.Natural exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Natural
import Maybe.Extra


type Natural
    = NonNegative Int


intValue : Natural -> Int
intValue (NonNegative int) =
    int


toString : Natural -> String
toString =
    intValue >> String.fromInt


fromString : String -> Maybe Natural
fromString s =
    String.toInt s
        |> Maybe.Extra.filter (\x -> x >= 0)
        |> Maybe.map NonNegative


zero : Natural
zero =
    NonNegative 0


min : Natural -> Natural -> Natural
min x y =
    if (x |> intValue) <= (y |> intValue) then
        x

    else
        y


toGraphQLInput : Natural -> LondoGQL.InputObject.NaturalInput
toGraphQLInput =
    intValue >> LondoGQL.InputObject.NaturalInput


selection : SelectionSet Natural LondoGQL.Object.Natural
selection =
    SelectionSet.map NonNegative LondoGQL.Object.Natural.nonNegative
