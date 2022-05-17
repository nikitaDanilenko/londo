module Types.Natural exposing (..)

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
