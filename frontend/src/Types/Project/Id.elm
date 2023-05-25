module Types.Project.Id exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.ProjectId
import LondoGQL.Scalar exposing (Uuid)


type Id
    = Id Uuid


uuid : Id -> Uuid
uuid (Id u) =
    u


toGraphQLInput : Id -> LondoGQL.InputObject.ProjectIdInput
toGraphQLInput =
    uuid >> LondoGQL.InputObject.ProjectIdInput


selection : SelectionSet Id LondoGQL.Object.ProjectId
selection =
    LondoGQL.Object.ProjectId.uuid |> SelectionSet.map Id
