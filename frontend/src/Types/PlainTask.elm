module Types.PlainTask exposing (..)

import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (PlainCreation, PlainUpdate)
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
