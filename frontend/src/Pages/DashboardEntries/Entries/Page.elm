module Pages.DashboardEntries.Entries.Page exposing (..)

import Language.Language
import Pages.Util.Choice.Page
import Pages.View.Tristate as Tristate
import Types.Dashboard.Id
import Types.DashboardEntry.Creation
import Types.DashboardEntry.Entry
import Types.Project.Id
import Types.Project.Project


type alias Model =
    Tristate.Model Main Initial


type alias DashboardId =
    Types.Dashboard.Id.Id


type alias ProjectId =
    Types.Project.Id.Id


type alias Project =
    Types.Project.Project.Project


type alias DashboardEntry =
    Types.DashboardEntry.Entry.Entry


type alias Creation =
    Types.DashboardEntry.Creation.ClientInput


type alias Update =
    Types.DashboardEntry.Entry.Entry


type alias Language =
    Language.Language.DashboardEntryEditor


type alias Initial =
    Pages.Util.Choice.Page.Initial DashboardId ProjectId DashboardEntry ProjectId Project Language


type alias Main =
    Pages.Util.Choice.Page.Main DashboardId ProjectId DashboardEntry Update ProjectId Project Creation Language


type alias LogicMsg =
    Pages.Util.Choice.Page.LogicMsg ProjectId DashboardEntry Update ProjectId Project Creation
