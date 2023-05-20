module Math.Positive exposing (Positive, fromString, intValue, one, oneHundred, selection, tenToTheNth, toGraphQLInput, toString)

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


{-| todo: This is awkward - it should only work with natural numbers.
-}
tenToTheNth : Int -> Positive
tenToTheNth n =
    if n <= 0 then
        one

    else
        10 ^ n |> Positive


oneHundred : Positive
oneHundred =
    Positive 100


times : Positive -> Positive -> Positive
times (Positive x) (Positive y) =
    Positive (x * y)


toGraphQLInput : Positive -> LondoGQL.InputObject.PositiveInput
toGraphQLInput =
    intValue >> LondoGQL.InputObject.PositiveInput


selection : SelectionSet Positive LondoGQL.Object.Positive
selection =
    SelectionSet.map Positive LondoGQL.Object.Positive.positive
