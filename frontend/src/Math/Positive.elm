module Math.Positive exposing (Positive, fromBigIntOrOne, fromInt, fromString, integerValue, one, oneHundred, selection, tenToTheNth, toGraphQLInput, toString)

import BigInt exposing (BigInt)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Positive
import LondoGQL.Scalar
import Math.Constants as Constants
import Maybe.Extra
import Util.GraphQLUtil as GraphQLUtil


type Positive
    = Positive BigInt


integerValue : Positive -> BigInt
integerValue (Positive int) =
    int


toString : Positive -> String
toString =
    integerValue >> BigInt.toString


fromString : String -> Maybe Positive
fromString =
    BigInt.fromIntString
        >> Maybe.Extra.filter (\x -> BigInt.gt x Constants.zeroBigInt)
        >> Maybe.map Positive


fromInt : Int -> Maybe Positive
fromInt =
    BigInt.fromInt
        >> Just
        >> Maybe.Extra.filter (\x -> BigInt.gt x Constants.zeroBigInt)
        >> Maybe.map Positive


one : Positive
one =
    Constants.oneBigInt |> Positive


fromBigIntOrOne : BigInt -> Positive
fromBigIntOrOne bi =
    if BigInt.lt bi Constants.oneBigInt then
        one

    else
        Positive bi


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
        (GraphQLUtil.bigIntFromGraphQL >> Maybe.Extra.unwrap one Positive)
        LondoGQL.Object.Positive.positive
