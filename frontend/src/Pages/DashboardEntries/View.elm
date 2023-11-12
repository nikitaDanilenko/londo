module Pages.DashboardEntries.View exposing (..)

import Configuration exposing (Configuration)
import Html exposing (Html)
import Pages.DashboardEntries.Dashboard.View
import Pages.DashboardEntries.Entries.View
import Pages.DashboardEntries.Page as Page
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate


view : Page.Model -> List (Html Page.Msg)
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Configuration -> Page.Main -> List (Html Page.LogicMsg)
viewMain configuration main =
    ViewUtil.viewMainWith
        { configuration = configuration
        , currentPage = Nothing
        , showNavigation = True
        , id = Style.ids.dashboardEntryEditor
        }
    <|
        [ Pages.DashboardEntries.Dashboard.View.viewMain configuration main.dashboard |> Html.map Page.DashboardMsg
        , Pages.DashboardEntries.Entries.View.viewEntries configuration main.entries |> Html.map Page.EntriesMsg
        , Pages.DashboardEntries.Entries.View.viewProjects configuration main.entries |> Html.map Page.EntriesMsg
        ]
