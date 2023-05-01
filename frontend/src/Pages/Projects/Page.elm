module Pages.Projects.Page exposing (..)

import Pages.Projects.Creation
import Pages.Projects.Project
import Pages.Projects.Update
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.ProjectId exposing (ProjectId)
import Util.DictList exposing (DictList)
import Util.Editing exposing (Editing)


type alias Model =
    Tristate.Model Main Initial


type alias Main =
    Pages.Util.ParentEditor.Page.Main
        ProjectId
        Pages.Projects.Project.Project
        Pages.Projects.Creation.ClientInput
        Pages.Projects.Update.ClientInput


type alias Initial =
    Pages.Util.ParentEditor.Page.Initial
        ProjectId
        Pages.Projects.Project.Project
        Pages.Projects.Update.ClientInput


type alias ProjectState =
    Editing
        Pages.Projects.Project.Project
        Pages.Projects.Update.ClientInput


type alias ProjectStateMap =
    DictList ProjectId ProjectState


type alias Flags =
    { authorizedAccess : AuthorizedAccess
    }


type alias Msg =
    Tristate.Msg LogicMsg


type alias LogicMsg =
    Pages.Util.ParentEditor.Page.LogicMsg
        ProjectId
        Pages.Projects.Project.Project
        Pages.Projects.Creation.ClientInput
        Pages.Projects.Update.ClientInput
