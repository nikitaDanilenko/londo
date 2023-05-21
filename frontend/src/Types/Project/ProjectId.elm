module Types.Project.ProjectId exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.ProjectId
import LondoGQL.Scalar exposing (Uuid)


type ProjectId
    = ProjectId Uuid


uuid : ProjectId -> Uuid
uuid (ProjectId u) =
    u


toGraphQLInput : ProjectId -> LondoGQL.InputObject.ProjectIdInput
toGraphQLInput =
    uuid >> LondoGQL.InputObject.ProjectIdInput


selection : SelectionSet ProjectId LondoGQL.Object.ProjectId
selection =
    LondoGQL.Object.ProjectId.uuid |> SelectionSet.map ProjectId
