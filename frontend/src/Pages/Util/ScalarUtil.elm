module Pages.Util.ScalarUtil exposing (..)

import LondoGQL.Scalar exposing (Uuid(..))


uuidToString : Uuid -> String
uuidToString (Uuid uuid) =
    uuid
