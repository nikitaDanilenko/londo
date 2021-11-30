module Pages.Util.ScalarUtil exposing (..)

import Integer
import LondoGQL.Scalar exposing (Natural(..), Positive(..), Uuid(..))
import Maybe.Extra


uuidToString : Uuid -> String
uuidToString (Uuid uuid) =
    uuid


positiveToString : Positive -> String
positiveToString (Positive positive) =
    positive


positiveToNatural : Positive -> Natural
positiveToNatural (Positive p) =
    Natural p


naturalToString : Natural -> String
naturalToString (Natural natural) =
    natural


stringToNatural : String -> Maybe Natural
stringToNatural s =
    Integer.fromString s
        |> Maybe.Extra.filter (Integer.lte Integer.zero)
        |> Maybe.map (always (Natural s))


stringToPositive : String -> Maybe Positive
stringToPositive s =
    Integer.fromString s
        |> Maybe.Extra.filter (Integer.lt Integer.zero)
        |> Maybe.map (always (Positive s))


zeroNatural : Natural
zeroNatural =
    Natural "0"


minNatural : Natural -> Natural -> Natural
minNatural a b =
    let
        asInteger =
            naturalToString
                >> Integer.fromString
                >> Maybe.withDefault Integer.zero
    in
    if Integer.lte (asInteger a) (asInteger b) then
        a

    else
        b
