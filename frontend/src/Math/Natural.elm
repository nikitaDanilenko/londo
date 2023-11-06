module Math.Natural exposing (Natural, fromBigIntOrZero, fromPositive, fromString, integerValue, min, one, selection, toGraphQLInput, toString, zero)

import BigInt exposing (BigInt)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Natural
import LondoGQL.Scalar
import Math.Constants as Constants
import Math.Positive
import Maybe.Extra
import Result.Extra
import Util.GraphQLUtil as GraphQLUtil


type Natural
    = NonNegative BigInt


integerValue : Natural -> BigInt
integerValue (NonNegative int) =
    int


toString : Natural -> String
toString =
    integerValue >> BigInt.toString


fromString : String -> Result String Natural
fromString =
    BigInt.fromIntString
        >> Result.fromMaybe "Not a representation of a natural number"
        >> Result.Extra.filter "Not a non-negative number" (\x -> BigInt.gte x (0 |> BigInt.fromInt))
        >> Result.map NonNegative


fromPositive : Math.Positive.Positive -> Natural
fromPositive =
    Math.Positive.integerValue >> NonNegative


zero : Natural
zero =
    Constants.zeroBigInt
        |> NonNegative


one : Natural
one =
    Constants.oneBigInt
        |> NonNegative


fromBigIntOrZero : BigInt -> Natural
fromBigIntOrZero bi =
    if BigInt.lt bi Constants.zeroBigInt then
        one

    else
        NonNegative bi


min : Natural -> Natural -> Natural
min x y =
    if BigInt.lte (x |> integerValue) (y |> integerValue) then
        x

    else
        y


toGraphQLInput : Natural -> LondoGQL.InputObject.NaturalInput
toGraphQLInput =
    toString >> LondoGQL.Scalar.BigInt >> LondoGQL.InputObject.NaturalInput


selection : SelectionSet Natural LondoGQL.Object.Natural
selection =
    SelectionSet.map
        (GraphQLUtil.bigIntFromGraphQL >> Maybe.Extra.unwrap zero NonNegative)
        LondoGQL.Object.Natural.nonNegative
