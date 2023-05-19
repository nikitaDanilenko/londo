module Math.Natural exposing (Natural, fromPositive, fromString, intValue, min, one, selection, toGraphQLInput, toString, zero)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.Natural
import Math.Positive
import Maybe.Extra
import Result.Extra


type Natural
    = NonNegative Int


intValue : Natural -> Int
intValue (NonNegative int) =
    int


toString : Natural -> String
toString =
    intValue >> String.fromInt


fromString : String -> Result String Natural
fromString =
    String.toInt
        >> Result.fromMaybe "Not a representation of a natural number"
        >> Result.Extra.filter "Not a non-negative number" (\x -> x >= 0)
        >> Result.map NonNegative


fromPositive : Math.Positive.Positive -> Natural
fromPositive =
    Math.Positive.intValue >> NonNegative


zero : Natural
zero =
    NonNegative 0


one : Natural
one =
    NonNegative 1


min : Natural -> Natural -> Natural
min x y =
    if (x |> intValue) <= (y |> intValue) then
        x

    else
        y


toGraphQLInput : Natural -> LondoGQL.InputObject.NaturalInput
toGraphQLInput =
    intValue >> LondoGQL.InputObject.NaturalInput


selection : SelectionSet Natural LondoGQL.Object.Natural
selection =
    SelectionSet.map NonNegative LondoGQL.Object.Natural.nonNegative
