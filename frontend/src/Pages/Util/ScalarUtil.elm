module Pages.Util.ScalarUtil exposing (..)

import LondoGQL.Scalar exposing (Natural (..), Positive (..), Uuid (..))


uuidToString : Uuid -> String
uuidToString (Uuid uuid) =
    uuid

positiveToString : Positive -> String
positiveToString (Positive positive) = positive

naturalToString : Natural -> String
naturalToString (Natural natural) = natural