module Pages.Dashboards.Page exposing (..)

import Language.Language
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.Dashboard.Creation
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Types.Dashboard.Update
import Util.DictList exposing (DictList)
import Util.Editing exposing (Editing)


type alias Model =
    Tristate.Model Main Initial


type alias Id =
    Types.Dashboard.Id.Id


type alias Dashboard =
    Types.Dashboard.Dashboard.Dashboard


type alias Creation =
    Types.Dashboard.Creation.ClientInput


type alias Update =
    Types.Dashboard.Update.ClientInput


type alias Language =
    Language.Language.DashboardEditor


type alias Main =
    Pages.Util.ParentEditor.Page.Main Id Dashboard Creation Update Language


type alias Initial =
    Pages.Util.ParentEditor.Page.Initial Id Dashboard Update Language


type alias DashboardState =
    Editing Dashboard Update


type alias DashboardStateMap =
    DictList Id DashboardState


type alias Flags =
    { authorizedAccess : AuthorizedAccess
    }


type alias Msg =
    Tristate.Msg LogicMsg


type alias LogicMsg =
    Pages.Util.ParentEditor.Page.LogicMsg Id Dashboard Creation Update
