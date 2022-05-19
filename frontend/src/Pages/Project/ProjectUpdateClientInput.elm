module Pages.Project.ProjectUpdateClientInput exposing (..)

-- todo: Allow changing project owner
-- todo: Allow changing project read/write access

import Monocle.Lens exposing (Lens)
import Pages.Project.ProjectInformation exposing (ProjectInformation)
import Types.Accessors as Accessors
import Types.ProjectId exposing (ProjectId)
import Types.UserId exposing (UserId)


type alias ProjectUpdateClientInput =
    { name : String
    , description : Maybe String
    , flatIfSingleTask : Bool
    }


to : ProjectId -> UserId -> ProjectUpdateClientInput -> ProjectInformation
to projectId ownerId input =
    { id = projectId
    , ownerId = ownerId
    , name = input.name
    , description = input.description
    , flatIfSingleTask = input.flatIfSingleTask

    -- todo #18: Handle accessors properly
    , readAccessors = Accessors.nobody |> Accessors.from
    , writeAccessors = Accessors.nobody |> Accessors.from
    }


from : ProjectInformation -> ProjectUpdateClientInput
from projectInformation =
    { name = projectInformation.name
    , description = projectInformation.description
    , flatIfSingleTask = projectInformation.flatIfSingleTask
    }


default : ProjectUpdateClientInput
default =
    { name = ""
    , description = Nothing
    , flatIfSingleTask = False
    }


name : Lens ProjectUpdateClientInput String
name =
    Lens .name (\b a -> { a | name = b })


description : Lens ProjectUpdateClientInput (Maybe String)
description =
    Lens .description (\b a -> { a | description = b })


flatIfSingleTask : Lens ProjectUpdateClientInput Bool
flatIfSingleTask =
    Lens .flatIfSingleTask (\b a -> { a | flatIfSingleTask = b })
