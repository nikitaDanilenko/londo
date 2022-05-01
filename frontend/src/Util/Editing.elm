module Util.Editing exposing (..)

import Monocle.Lens exposing (Lens)


type alias Editing a b =
    { original : a
    , update : b
    }


updateLens : Lens (Editing a b) b
updateLens =
    Lens .update (\b a -> { a | update = b })
