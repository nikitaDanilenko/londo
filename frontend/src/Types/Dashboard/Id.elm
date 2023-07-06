module Types.Dashboard.Id exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.DashboardId
import LondoGQL.Scalar exposing (Uuid)


type Id
    = Id Uuid


uuid : Id -> Uuid
uuid (Id u) =
    u


toGraphQLInput : Id -> LondoGQL.InputObject.DashboardIdInput
toGraphQLInput =
    uuid >> LondoGQL.InputObject.DashboardIdInput


selection : SelectionSet Id LondoGQL.Object.DashboardId
selection =
    LondoGQL.Object.DashboardId.uuid |> SelectionSet.map Id
