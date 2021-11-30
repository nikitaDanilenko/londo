module Pages.Util.ScalarUtil exposing (..)

import Integer
import LondoGQL.Scalar exposing (Natural(..), Positive(..), Uuid(..))


uuidToString : Uuid -> String
uuidToString (Uuid uuid) =
    uuid


positiveToString : Positive -> String
positiveToString (Positive positive) =
    positive


naturalToString : Natural -> String
naturalToString (Natural natural) =
    natural


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
