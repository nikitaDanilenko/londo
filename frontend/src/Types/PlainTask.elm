module Types.PlainTask exposing (..)

import Graphql.OptionalArgument as OptionalArgumentUtil
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (PlainUpdate)
import LondoGQL.Scalar exposing (Positive)
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


toUpdate : PlainTask -> PlainUpdate
toUpdate plainTask =
    { name = plainTask.name
    , taskKind = plainTask.taskKind
    , unit = OptionalArgumentUtil.fromMaybe plainTask.unit
    , weight = plainTask.weight
    }
