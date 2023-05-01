module Pages.Projects.Update exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject
import Monocle.Lens exposing (Lens)
import Pages.Projects.Project
import Pages.Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)
import Types.ProjectId as ProjectId exposing (ProjectId)


type alias ClientInput =
    { name : ValidatedInput String
    , description : Maybe String
    }


lenses :
    { name : Lens ClientInput (ValidatedInput String)
    , description : Lens ClientInput (Maybe String)
    }
lenses =
    { name = Lens .name (\b a -> { a | name = b })
    , description = Lens .description (\b a -> { a | description = b })
    }


from : Pages.Projects.Project.Project -> ClientInput
from project =
    { name =
        ValidatedInput.nonEmptyString
            |> ValidatedInput.lenses.value.set project.name
            |> ValidatedInput.lenses.text.set project.name
    , description = project.description
    }


to : ProjectId -> ClientInput -> LondoGQL.InputObject.UpdateProjectInput
to projectId input =
    { projectId =
        projectId
            |> ProjectId.uuid
            |> LondoGQL.InputObject.ProjectIdInput
    , name = input.name.value
    , description = input.description |> OptionalArgument.fromMaybe
    }
