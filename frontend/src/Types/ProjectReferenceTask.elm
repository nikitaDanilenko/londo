module Types.ProjectReferenceTask exposing (..)

import LondoGQL.InputObject exposing (ProjectReferenceUpdate)
import LondoGQL.Scalar exposing (Positive)
import Types.ProjectId as ProjectId exposing (ProjectId)
import Types.TaskId exposing (TaskId)


type alias ProjectReferenceTask =
    { id : TaskId
    , projectReferenceId : ProjectId
    , weight : Positive
    }


toUpdate : ProjectReferenceTask -> ProjectReferenceUpdate
toUpdate projectReferenceTask =
    { projectReferenceId =
        { uuid = ProjectId.uuid projectReferenceTask.projectReferenceId
        }
    , weight = projectReferenceTask.weight
    }
