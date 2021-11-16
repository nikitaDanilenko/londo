module Pages.Project.PlainCreationClientInput exposing (..)

import Graphql.OptionalArgument exposing (OptionalArgument)
import LondoGQL.Enum.TaskKind
import LondoGQL.InputObject exposing (PlainCreation, ProgressInput)
import LondoGQL.Scalar exposing (Natural, Positive)
import Pages.Util.FromInput exposing (FromInput)


type alias PlainCreationClientInput =
    { name : String
    , taskKind : LondoGQL.Enum.TaskKind.TaskKind
    , unit : OptionalArgument String
    , progress : ProgressClientInput
    , weight : FromInput Positive
    }


type alias ProgressClientInput =
    { reachable : FromInput Positive
    , reached : FromInput Natural
    }


toProgressInput : ProgressClientInput -> ProgressInput
toProgressInput input =
    { reachable = input.reachable.value
    , reached = input.reached.value
    }


toPlainCreation : PlainCreationClientInput -> PlainCreation
toPlainCreation input =
    { name = input.name
    , taskKind = input.taskKind
    , unit = input.unit
    , progress = toProgressInput input.progress
    , weight = input.weight.value
    }
