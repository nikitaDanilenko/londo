module Pages.Project.PlainUpdateClientInput exposing (..)

import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import Graphql.OptionalArgument as OptionalArgument exposing (OptionalArgument)
import LondoGQL.Enum.TaskKind as TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (PlainCreation, ProgressInput)
import LondoGQL.Scalar exposing (Natural, Positive)
import Monocle.Lens exposing (Lens)
import Pages.Project.ProgressClientInput as ProgressClientInput exposing (ProgressClientInput)
import Pages.Util.FromInput as FromInput exposing (FromInput)
import Types.PlainTask exposing (PlainTask)


type alias PlainUpdateClientInput =
    { name : String
    , taskKind : TaskKind.TaskKind
    , unit : OptionalArgument String
    , progress : ProgressClientInput
    , weight : FromInput Positive
    }


to : PlainUpdateClientInput -> PlainCreation
to input =
    { name = input.name
    , taskKind = input.taskKind
    , unit = input.unit
    , progress = ProgressClientInput.to input.progress
    , weight = input.weight.value
    }


from : PlainTask -> PlainUpdateClientInput
from plainTask =
    { name = plainTask.name
    , taskKind = plainTask.taskKind
    , unit = OptionalArgument.fromMaybe plainTask.unit
    , progress = ProgressClientInput.fromProgress plainTask.progress
    , weight = FromInput.value.set plainTask.weight FromInput.positive
    }


default : PlainUpdateClientInput
default =
    { name = ""
    , taskKind = TaskKind.Fractional
    , unit = OptionalArgument.Absent
    , progress = ProgressClientInput.default
    , weight = FromInput.positive
    }


name : Lens PlainUpdateClientInput String
name =
    Lens .name (\b a -> { a | name = b })


taskKind : Lens PlainUpdateClientInput TaskKind
taskKind =
    Lens .taskKind (\b a -> { a | taskKind = b })


unit : Lens PlainUpdateClientInput (Maybe String)
unit =
    Lens (.unit >> OptionalArgumentUtil.toMaybe) (\b a -> { a | unit = OptionalArgument.fromMaybe b })


progress : Lens PlainUpdateClientInput ProgressClientInput
progress =
    Lens .progress (\b a -> { a | progress = b })


weight : Lens PlainUpdateClientInput (FromInput Positive)
weight =
    Lens .weight (\b a -> { a | weight = b })
