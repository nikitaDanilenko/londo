module GraphQLFunctions.Lens.ProgressInput exposing (..)

import LondoGQL.InputObject exposing (ProgressInput)
import LondoGQL.Scalar exposing (Natural, Positive)
import Monocle.Lens exposing (Lens)


reached : Lens ProgressInput Natural
reached =
    Lens .reached (\b a -> { a | reached = b })


reachable : Lens ProgressInput Positive
reachable =
    Lens .reachable (\b a -> { a | reachable = b })
