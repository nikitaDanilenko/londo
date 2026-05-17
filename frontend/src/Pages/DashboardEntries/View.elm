module Pages.DashboardEntries.View exposing (..)

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


viewMain : Page.Main -> List (Html Page.LogicMsg)
viewMain main =
    ViewUtil.viewMainWith
        { currentPage = Nothing
        , showNavigation = True
        , id = Style.ids.dashboardEntryEditor
        }
    <|
        [ Pages.DashboardEntries.Dashboard.View.viewMain main.dashboard |> Html.map Page.DashboardMsg
        , Pages.DashboardEntries.Entries.View.viewEntries main.entries |> Html.map Page.EntriesMsg
        , Pages.DashboardEntries.Entries.View.viewProjects main.entries |> Html.map Page.EntriesMsg
        ]
