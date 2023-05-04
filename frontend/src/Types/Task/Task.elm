module Types.Task.Task exposing (..)

import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (TaskCreation, TaskUpdate)
import Types.Task.Progress exposing (Progress)
import Types.TaskId exposing (TaskId)


type alias Task =
    { id : TaskId
    , name : String
    , taskKind : TaskKind
    , unit : Maybe String
    , progress : Progress
    , counting: Bool
    }


fromCreation : TaskId -> TaskCreation -> Task
fromCreation id taskCreation =
    { id = id
    , name = taskCreation.name
    , taskKind = taskCreation.taskKind
    , unit = OptionalArgumentUtil.toMaybe taskCreation.unit
    , progress = taskCreation.progress
    , counting = taskCreation.counting
    }
