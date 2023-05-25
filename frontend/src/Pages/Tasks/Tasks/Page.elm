module Pages.Tasks.Tasks.Page exposing (..)

import Language.Language
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.Project.ProjectId exposing (ProjectId)
import Types.Task.Creation
import Types.Task.Id
import Types.Task.Task
import Types.Task.Update


type alias Model =
    { projectId : ProjectId
    , subModel : Tristate.Model SubMain SubInitial
    }


type alias SubModel =
    Tristate.Model SubMain SubInitial


type alias TaskId =
    Types.Task.Id.Id


type alias Task =
    Types.Task.Task.Task


type alias Creation =
    Types.Task.Creation.ClientInput


type alias Update =
    Types.Task.Update.ClientInput


type alias Language =
    Language.Language.TaskEditor


type alias SubInitial =
    Pages.Util.ParentEditor.Page.Initial TaskId Task Update Language


type alias SubMain =
    Pages.Util.ParentEditor.Page.Main TaskId Task Creation Update Language


lenses : { subModel : Lens Model (Tristate.Model SubMain SubInitial) }
lenses =
    { subModel = Lens .subModel (\b a -> { a | subModel = b })
    }


subInitial : AuthorizedAccess -> SubInitial
subInitial authorizedAccess =
    Pages.Util.ParentEditor.Page.defaultInitial
        authorizedAccess.jwt
        Language.Language.default.taskEditor


type alias Msg =
    Tristate.Msg LogicMsg


type alias LogicMsg =
    Pages.Util.ParentEditor.Page.LogicMsg TaskId Task Creation Update
