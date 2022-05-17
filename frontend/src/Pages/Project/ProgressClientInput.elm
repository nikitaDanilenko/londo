module Pages.Project.ProgressClientInput exposing (..)

import LondoGQL.InputObject exposing (ProgressInput)
import Monocle.Lens exposing (Lens)
import Pages.Util.FromInput as FromInput exposing (FromInput)
import Types.Natural as Natural exposing (Natural)
import Types.Positive as Positive exposing (Positive)
import Types.Progress exposing (Progress)


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
            |> FromInput.text.set (Positive.toString fi.value.reachable)
    , reached =
        FromInput.natural
            |> FromInput.value.set fi.value.reached
            |> FromInput.text.set (Natural.toString fi.value.reached)
    }


to : ProgressClientInput -> ProgressInput
to input =
    { reachable = input.reachable.value
    , reached = input.reached.value
    }


fromProgress : Progress -> ProgressClientInput
fromProgress p =
    { reachable = FromInput.value.set p.reachable FromInput.positive
    , reached = FromInput.value.set p.reached FromInput.natural
    }


reachable : Lens ProgressClientInput (FromInput Positive)
reachable =
    Lens .reachable (\b a -> { a | reachable = b })


reached : Lens ProgressClientInput (FromInput Natural)
reached =
    Lens .reached (\b a -> { a | reached = b })
