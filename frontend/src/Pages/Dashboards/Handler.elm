module Pages.Dashboards.Handler exposing (..)

import Addresses.Frontend
import Language.Language
import Pages.Dashboards.Page as Page
import Pages.Util.ParentEditor.Handler
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.Dashboard.Creation
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Types.Dashboard.Update


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Pages.Util.ParentEditor.Page.initial
        flags.authorizedAccess
        Language.Language.default.dashboardEditor
        Language.Language.default.errorHandling
    , Types.Dashboard.Dashboard.fetchAllWith
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
        , parentIdOrdering = Types.Dashboard.Id.ordering
        , toUpdate = Types.Dashboard.Update.from
        , navigateToAddress = Addresses.Frontend.dashboardEntries.address >> Just
        , create = Types.Dashboard.Creation.createWith Pages.Util.ParentEditor.Page.GotCreateResponse
        , save = Types.Dashboard.Update.updateWith Pages.Util.ParentEditor.Page.GotSaveEditResponse
        , delete = Types.Dashboard.Dashboard.deleteWith Pages.Util.ParentEditor.Page.GotDeleteResponse
        }
