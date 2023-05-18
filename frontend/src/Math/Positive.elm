module Math.Positive exposing (Positive, fromString, intValue, one, oneHundred, selection, toGraphQLInput, toString)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Positive
import Maybe.Extra


type Positive
    = Positive Int


intValue : Positive -> Int
intValue (Positive int) =
    int


toString : Positive -> String
toString =
    intValue >> String.fromInt


fromString : String -> Maybe Positive
fromString s =
    String.toInt s
        |> Maybe.Extra.filter (\x -> x > 0)
        |> Maybe.map Positive


one : Positive
one =
    Positive 1


oneHundred : Positive
oneHundred =
    Positive 100


toGraphQLInput : Positive -> LondoGQL.InputObject.PositiveInput
toGraphQLInput =
    intValue >> LondoGQL.InputObject.PositiveInput


selection : SelectionSet Positive LondoGQL.Object.Positive
selection =
    SelectionSet.map Positive LondoGQL.Object.Positive.positive
