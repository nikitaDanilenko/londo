module Types.Progress exposing (..)

import Monocle.Lens exposing (Lens)
import Types.Natural exposing (Natural)
import Types.Positive exposing (Positive)


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
