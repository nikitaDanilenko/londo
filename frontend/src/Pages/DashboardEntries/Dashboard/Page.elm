module Pages.DashboardEntries.Dashboard.Page exposing (..)

import Language.Language
import Pages.Util.Parent.Page
import Pages.View.Tristate as Tristate
import Types.Dashboard.Dashboard
import Types.Dashboard.Update


type alias Model =
    Tristate.Model Main Initial


type alias Dashboard =
    Types.Dashboard.Dashboard.Dashboard


type alias Update =
    Types.Dashboard.Update.ClientInput


type alias Language =
    Language.Language.DashboardEditor


type alias Main =
    Pages.Util.Parent.Page.Main Dashboard Update Language


type alias Initial =
    Pages.Util.Parent.Page.Initial Dashboard Language


type alias LogicMsg =
    Pages.Util.Parent.Page.LogicMsg Dashboard Update
