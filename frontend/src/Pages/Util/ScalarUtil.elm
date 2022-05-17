module Pages.Util.ScalarUtil exposing (..)

import LondoGQL.Scalar exposing (Uuid(..))



--todo: Check usefulness


uuidToString : Uuid -> String
uuidToString (Uuid uuid) =
    uuid
