module Math.Positive exposing (Positive, fromString, integerValue, one, oneHundred, selection, sum, tenToTheNth, toGraphQLInput, toString)

import BigInt exposing (BigInt)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Positive
import LondoGQL.Scalar
import Math.Constants as Constants
import Maybe.Extra


type Positive
    = Positive BigInt


sum : List Positive -> BigInt
sum =
    List.foldl (\x acc -> BigInt.add acc (x |> integerValue)) (BigInt.fromInt 0)


integerValue : Positive -> BigInt
integerValue (Positive int) =
    int


toString : Positive -> String
toString =
    integerValue >> BigInt.toString


fromString : String -> Maybe Positive
fromString s =
    BigInt.fromIntString s
        |> Maybe.Extra.filter (\x -> BigInt.gt x Constants.zeroBigInt)
        |> Maybe.map Positive


one : Positive
one =
    Constants.oneBigInt |> Positive


{-| todo: This is awkward - it should only work with natural numbers.
-}
tenToTheNth : Int -> Positive
tenToTheNth n =
    "1"
        ++ String.repeat n "0"
        |> BigInt.fromIntString
        |> Maybe.Extra.unwrap one Positive


oneHundred : Positive
oneHundred =
    Constants.oneHundredBigInt
        |> Positive


toGraphQLInput : Positive -> LondoGQL.InputObject.PositiveInput
toGraphQLInput =
    toString >> LondoGQL.Scalar.BigInt >> LondoGQL.InputObject.PositiveInput


selection : SelectionSet Positive LondoGQL.Object.Positive
selection =
    SelectionSet.map
        --todo: Extract conversion from BigInt to Integer?
        ((\(LondoGQL.Scalar.BigInt str) -> BigInt.fromIntString str)
            >> Maybe.Extra.unwrap one Positive
        )
        LondoGQL.Object.Positive.positive
