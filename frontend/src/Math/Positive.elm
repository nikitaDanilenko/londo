module Math.Positive exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Positive
import Maybe.Extra


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


one : Positive
one =
    { positive = 1 }


oneHundred : Positive
oneHundred =
    { positive = 100 }


toGraphQLInput : Positive -> LondoGQL.InputObject.PositiveInput
toGraphQLInput =
    identity


selection : SelectionSet Positive LondoGQL.Object.Positive
selection =
    SelectionSet.map Positive LondoGQL.Object.Positive.positive
