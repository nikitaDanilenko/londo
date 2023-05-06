module Types.Project.ProjectId exposing (..)

import LondoGQL.InputObject
import LondoGQL.Scalar exposing (Uuid)


type ProjectId
    = ProjectId Uuid


uuid : ProjectId -> Uuid
uuid (ProjectId u) =
    u


toGraphQLInput : ProjectId -> LondoGQL.InputObject.ProjectIdInput
toGraphQLInput =
    uuid >> LondoGQL.InputObject.ProjectIdInput
