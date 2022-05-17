module Types.Positive exposing (..)

import Maybe.Extra
import Types.Natural exposing (Natural)


type alias Positive =
    { positive : Int }


toString : Positive -> String
toString =
    .positive >> String.fromInt


fromString : String -> Maybe Positive
fromString s =
    String.toInt s
        |> Maybe.Extra.filter (\x -> x > 0)
        |> Maybe.map (\p -> { positive = p })


toNatural : Positive -> Natural
toNatural p =
    { nonNegative = p.positive }


one : Positive
one =
    { positive = 1 }


oneHundred : Positive
oneHundred =
    { positive = 100 }
