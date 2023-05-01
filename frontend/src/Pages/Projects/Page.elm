module Pages.Projects.Page exposing (..)

import Language.Language
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.Project.Creation
import Types.Project.Project
import Types.Project.ProjectId
import Types.Project.Update
import Util.DictList exposing (DictList)
import Util.Editing exposing (Editing)


type alias Model =
    Tristate.Model Main Initial


type alias Id =
    Types.Project.ProjectId.ProjectId


type alias Project =
    Types.Project.Project.Project


type alias Creation =
    Types.Project.Creation.ClientInput


type alias Update =
    Types.Project.Update.ClientInput

type alias Language =
    Language.Language.ProjectEditor

type alias Main =
    Pages.Util.ParentEditor.Page.Main Id Project Creation Update Language


type alias Initial =
    Pages.Util.ParentEditor.Page.Initial Id Project Update Language


type alias ProjectState =
    Editing Project Update


type alias ProjectStateMap =
    DictList Id ProjectState


type alias Flags =
    { authorizedAccess : AuthorizedAccess
    }


type alias Msg =
    Tristate.Msg LogicMsg


type alias LogicMsg =
    Pages.Util.ParentEditor.Page.LogicMsg Id Project Creation Update
