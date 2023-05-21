module Math.Natural exposing (Natural, fromPositive, fromString, integerValue, min, one, selection, toGraphQLInput, toString, zero)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import Integer exposing (Integer)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Natural
import LondoGQL.Scalar
import Math.Positive
import Result.Extra


type Natural
    = NonNegative Integer


integerValue : Natural -> Integer
integerValue (NonNegative int) =
    int


toString : Natural -> String
toString =
    integerValue >> Integer.toString


fromString : String -> Result String Natural
fromString =
    Integer.fromString
        >> Result.fromMaybe "Not a representation of a natural number"
        >> Result.Extra.filter "Not a non-negative number" (\x -> Integer.gte x Integer.zero)
        >> Result.map NonNegative


fromPositive : Math.Positive.Positive -> Natural
fromPositive =
    Math.Positive.integerValue >> NonNegative


zero : Natural
zero =
    NonNegative Integer.zero


one : Natural
one =
    NonNegative Integer.one


min : Natural -> Natural -> Natural
min x y =
    if Integer.lte (x |> integerValue) (y |> integerValue) then
        x

    else
        y


toGraphQLInput : Natural -> LondoGQL.InputObject.NaturalInput
toGraphQLInput =
    toString >> LondoGQL.Scalar.BigInt >> LondoGQL.InputObject.NaturalInput


selection : SelectionSet Natural LondoGQL.Object.Natural
selection =
    SelectionSet.map
        ((\(LondoGQL.Scalar.BigInt str) -> Integer.fromString str)
            >> Maybe.withDefault Integer.zero
            >> NonNegative
        )
        LondoGQL.Object.Natural.nonNegative
