module Pages.Project.ProgressClientInput exposing (..)

import LondoGQL.InputObject exposing (ProgressInput)
import LondoGQL.Scalar exposing (Natural, Positive)
import Pages.Util.FromInput as FromInput exposing (FromInput)


type alias ProgressClientInput =
    { reachable : FromInput Positive
    , reached : FromInput Natural
    }


default : ProgressClientInput
default =
    { reachable = FromInput.positive
    , reached = FromInput.natural
    }


to : ProgressClientInput -> ProgressInput
to input =
    { reachable = input.reachable.value
    , reached = input.reached.value
    }
