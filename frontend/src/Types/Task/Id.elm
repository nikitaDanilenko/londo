module Types.Task.Id exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.TaskId
import LondoGQL.Scalar exposing (Uuid)
import Util.GraphQLUtil as GraphQLUtil
import Util.Ordering as Ordering exposing (Ordering)


type Id
    = Id Uuid


uuid : Id -> Uuid
uuid (Id id) =
    id


toGraphQLInput : Id -> LondoGQL.InputObject.TaskIdInput
toGraphQLInput =
    uuid >> LondoGQL.InputObject.TaskIdInput


selection : SelectionSet Id LondoGQL.Object.TaskId
selection =
    SelectionSet.map Id LondoGQL.Object.TaskId.uuid


ordering : Ordering Id
ordering =
    Ordering.with (uuid >> GraphQLUtil.uuidToString)
