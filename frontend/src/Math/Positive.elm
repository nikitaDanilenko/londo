module Math.Positive exposing (Positive, fromString, integerValue, one, oneHundred, selection, tenToTheNth, toGraphQLInput, toString)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import Integer exposing (Integer)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Positive
import LondoGQL.Scalar
import Maybe.Extra


type Positive
    = Positive Integer


integerValue : Positive -> Integer
integerValue (Positive int) =
    int


toString : Positive -> String
toString =
    integerValue >> Integer.toString


fromString : String -> Maybe Positive
fromString s =
    Integer.fromString s
        |> Maybe.Extra.filter (\x -> Integer.gt x Integer.zero)
        |> Maybe.map Positive


one : Positive
one =
    Positive Integer.one


{-| todo: This is awkward - it should only work with natural numbers.
-}
tenToTheNth : Int -> Positive
tenToTheNth n =
    "1" ++ String.repeat n "0" |> Integer.fromString |> Maybe.withDefault Integer.one |> Positive


oneHundred : Positive
oneHundred =
    Positive Integer.hundred


toGraphQLInput : Positive -> LondoGQL.InputObject.PositiveInput
toGraphQLInput =
    toString >> LondoGQL.Scalar.BigInt >> LondoGQL.InputObject.PositiveInput


selection : SelectionSet Positive LondoGQL.Object.Positive
selection =
    SelectionSet.map
        --todo: Extract conversion from BigInt to Integer?
        ((\(LondoGQL.Scalar.BigInt str) -> Integer.fromString str)
            >> Maybe.withDefault Integer.one
            >> Positive
        )
        LondoGQL.Object.Positive.positive
