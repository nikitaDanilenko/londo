module Types.TaskId exposing (..)

import LondoGQL.Scalar exposing (Uuid)


type TaskId
    = TaskId Uuid
