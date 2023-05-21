module Types.Task.TaskId exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.TaskId
import LondoGQL.Scalar exposing (Uuid)


type TaskId
    = TaskId Uuid


uuid : TaskId -> Uuid
uuid (TaskId id) =
    id


toGraphQLInput : TaskId -> LondoGQL.InputObject.TaskIdInput
toGraphQLInput =
    uuid >> LondoGQL.InputObject.TaskIdInput


selection : SelectionSet TaskId LondoGQL.Object.TaskId
selection =
    SelectionSet.map TaskId LondoGQL.Object.TaskId.uuid
