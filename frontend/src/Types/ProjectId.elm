module Types.ProjectId exposing (..)

import LondoGQL.InputObject
import LondoGQL.Scalar exposing (Uuid)


type ProjectId
    = ProjectId Uuid


uuid : ProjectId -> Uuid
uuid (ProjectId u) =
    u

toInput : ProjectId -> LondoGQL.InputObject.ProjectIdInput
toInput = uuid >> LondoGQL.InputObject.ProjectIdInput