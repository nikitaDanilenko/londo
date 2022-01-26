module Types.ProjectId exposing (..)

import LondoGQL.Scalar exposing (Uuid)


type ProjectId
    = ProjectId Uuid


uuid : ProjectId -> Uuid
uuid (ProjectId u) =
    u
