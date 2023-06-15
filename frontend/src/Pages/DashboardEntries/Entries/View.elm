module Pages.DashboardEntries.Entries.View exposing (..)

import Configuration exposing (Configuration)
import Html exposing (Html)
import Pages.DashboardEntries.Entries.Page as Page
import Pages.Util.Choice.View
import Util.SearchUtil as SearchUtil


viewEntries : Configuration -> Page.Main -> Html Page.LogicMsg
viewEntries configuration main =
    Pages.Util.Choice.View.viewElements
        { nameOfChoice = .name
        , choiceIdOfElement = .projectId
        , -- todo: Or id?
          idOfElement = .id
        , elementHeaderColumns = []
        , info =
            \entry ->
                { display = []
                , controls = []
                }
        , isValidInput = always True
        , edit =
            \dashboardEntry input ->
                []
        }
        main


viewProjects : Configuration -> Page.Main -> Html Page.LogicMsg
viewProjects configuration main =
    Pages.Util.Choice.View.viewChoices
        { matchesSearchText = \string project -> SearchUtil.search string project.name || SearchUtil.search string (project.description |> Maybe.withDefault "")
        , sortBy = .name
        , choiceHeaderColumns = []
        , idOfChoice = .id
        , elementCreationLine =
            \project creation ->
                { display = []
                , controls = []
                }
        , viewChoiceLine =
            \project ->
                { display = []
                , controls = []
                }
        }
        main
