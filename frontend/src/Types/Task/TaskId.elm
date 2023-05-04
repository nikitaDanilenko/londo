module Types.Task.TaskId exposing (..)

import LondoGQL.InputObject
import LondoGQL.Scalar exposing (Uuid)


type TaskId
    = TaskId Uuid


uuid : TaskId -> Uuid
uuid (TaskId id) =
    id


toInput : TaskId -> LondoGQL.InputObject.TaskIdInput
toInput =
    uuid >> LondoGQL.InputObject.TaskIdInput
