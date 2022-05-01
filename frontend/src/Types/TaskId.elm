module Types.TaskId exposing (..)

import LondoGQL.Scalar exposing (Uuid)


type TaskId
    = TaskId Uuid

uuid: TaskId -> Uuid
uuid (TaskId id) = id