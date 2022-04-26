module Pages.Project.ProjectReferenceUpdateClientInput exposing (..)

import LondoGQL.Scalar exposing (Positive)
import Monocle.Lens exposing (Lens)
import Pages.Util.FromInput exposing (FromInput)
import Types.ProjectId exposing (ProjectId)


type alias ProjectReferenceUpdateClientInput =
    { projectReferenceId : ProjectId
    , weight : FromInput Positive
    }


projectReferenceId : Lens ProjectReferenceUpdateClientInput ProjectId
projectReferenceId =
    Lens .projectReferenceId (\b a -> { a | projectReferenceId = b })


weight : Lens ProjectReferenceUpdateClientInput (FromInput Positive)
weight =
    Lens .weight (\b a -> { a | weight = b })