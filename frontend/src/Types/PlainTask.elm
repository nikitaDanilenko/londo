module Types.PlainTask exposing (..)

import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (PlainCreation, PlainUpdate)
import Monocle.Lens exposing (Lens)
import Types.Positive exposing (Positive)
import Types.Progress exposing (Progress)
import Types.TaskId exposing (TaskId)


type alias PlainTask =
    { id : TaskId
    , name : String
    , taskKind : TaskKind
    , unit : Maybe String
    , progress : Progress
    , weight : Positive
    }


fromCreation : TaskId -> PlainCreation -> PlainTask
fromCreation id plainCreation =
    { id = id
    , name = plainCreation.name
    , taskKind = plainCreation.taskKind
    , unit = OptionalArgumentUtil.toMaybe plainCreation.unit
    , progress = plainCreation.progress
    , weight = plainCreation.weight
    }


toUpdate : PlainTask -> PlainUpdate
toUpdate plainTask =
    { name = plainTask.name
    , taskKind = plainTask.taskKind
    , unit = OptionalArgument.fromMaybe plainTask.unit
    , weight = plainTask.weight
    , progressUpdate = plainTask.progress
    }


name : Lens PlainTask String
name =
    Lens .name (\b a -> { a | name = b })


taskKind : Lens PlainTask TaskKind
taskKind =
    Lens .taskKind (\b a -> { a | taskKind = b })


unit : Lens PlainTask (Maybe String)
unit =
    Lens .unit (\b a -> { a | unit = b })


progress : Lens PlainTask Progress
progress =
    Lens .progress (\b a -> { a | progress = b })


weight : Lens PlainTask Positive
weight =
    Lens .weight (\b a -> { a | weight = b })
