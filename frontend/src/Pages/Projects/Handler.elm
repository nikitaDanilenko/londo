module Pages.Projects.Handler exposing (init, update)

import Addresses.Frontend
import Language.Language
import Pages.Projects.Page as Page
import Pages.Util.ParentEditor.Handler
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.Project.Creation
import Types.Project.Project
import Types.Project.Update


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Pages.Util.ParentEditor.Page.initial flags.authorizedAccess Language.Language.default.projectEditor
    , Types.Project.Project.fetchAllWith
        Pages.Util.ParentEditor.Page.GotFetchResponse
        flags.authorizedAccess
        |> Cmd.map Tristate.Logic
    )


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic =
    Pages.Util.ParentEditor.Handler.updateLogic
        { idOfParent = .id
        , toUpdate = Types.Project.Update.from
        , navigateToAddress = Addresses.Frontend.taskEditor.address >> Just
        , create = Types.Project.Creation.createWith Pages.Util.ParentEditor.Page.GotCreateResponse
        , save = Types.Project.Update.updateWith Pages.Util.ParentEditor.Page.GotSaveEditResponse
        , delete = Types.Project.Project.deleteWith Pages.Util.ParentEditor.Page.GotDeleteResponse
        }
