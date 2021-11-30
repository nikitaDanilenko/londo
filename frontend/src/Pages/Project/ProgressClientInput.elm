module Pages.Project.ProgressClientInput exposing (..)

import LondoGQL.InputObject exposing (ProgressInput)
import LondoGQL.Scalar exposing (Natural, Positive)
import Monocle.Lens exposing (Lens)
import Pages.Util.FromInput as FromInput exposing (FromInput)
import Pages.Util.ScalarUtil as ScalarUtil


type alias ProgressClientInput =
    { reachable : FromInput Positive
    , reached : FromInput Natural
    }


default : ProgressClientInput
default =
    { reachable = FromInput.positive
    , reached = FromInput.natural
    }


from : FromInput ProgressInput -> ProgressClientInput
from fi =
    { reachable =
        FromInput.positive
            |> FromInput.value.set fi.value.reachable
            |> FromInput.text.set (ScalarUtil.positiveToString fi.value.reachable)
    , reached =
        FromInput.natural
            |> FromInput.value.set fi.value.reached
            |> FromInput.text.set (ScalarUtil.naturalToString fi.value.reached)
    }


to : ProgressClientInput -> ProgressInput
to input =
    { reachable = input.reachable.value
    , reached = input.reached.value
    }


reachable : Lens ProgressClientInput (FromInput Positive)
reachable =
    Lens .reachable (\b a -> { a | reachable = b })


reached : Lens ProgressClientInput (FromInput Natural)
reached =
    Lens .reached (\b a -> { a | reached = b })
