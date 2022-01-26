module Types.Progress exposing (..)

import LondoGQL.Scalar exposing (Natural, Positive)
import Monocle.Lens exposing (Lens)


type alias Progress =
    { reachable : Positive
    , reached : Natural
    }


reachable : Lens Progress Positive
reachable =
    Lens .reachable (\b a -> { a | reachable = b })


reached : Lens Progress Natural
reached =
    Lens .reached (\b a -> { a | reached = b })
