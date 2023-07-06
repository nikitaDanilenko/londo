module Pages.DashboardEntries.Entries.View exposing (..)

import Configuration exposing (Configuration)
import Html exposing (Html, button, label, td, text, th)
import Html.Events exposing (onClick)
import Maybe.Extra
import Pages.DashboardEntries.Entries.Page as Page
import Pages.Projects.View
import Pages.Util.Choice.Page
import Pages.Util.Choice.View
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.NavigationUtil as NavigationUtil
import Pages.Util.Style as Style
import Types.DashboardEntry.Creation
import Types.Project.Id
import Types.Project.Project
import Util.DictList as DictList exposing (DictList)
import Util.Editing exposing (Editing)
import Util.SearchUtil as SearchUtil


viewEntries : Configuration -> Page.Main -> Html Page.LogicMsg
viewEntries configuration main =
    Pages.Util.Choice.View.viewElements
        { nameOfChoice = .name
        , choiceIdOfElement = .projectId
        , idOfElement = .projectId
        , elementHeaderColumns =
            [ th [] [ label [] [ text <| .dashboardEntryName <| main.language ] ]
            , th [] [ label [] [ text <| .dashboardEntryDescription <| main.language ] ]
            ]
        , info =
            \entry ->
                { display = projectInfoFromMap main.choices entry.projectId
                , controls =
                    [ td [ Style.classes.controls ] [ button [ Style.classes.button.edit, onClick <| Pages.Util.Choice.Page.EnterEdit <| entry.projectId ] [ text <| .edit <| main.language ] ]
                    , td [ Style.classes.controls ] [ button [ Style.classes.button.delete, onClick <| Pages.Util.Choice.Page.RequestDelete <| entry.projectId ] [ text <| .delete <| main.language ] ]
                    , td [ Style.classes.controls ] [ NavigationUtil.projectEditorLinkButton configuration entry.projectId <| .taskEditor <| main.language ]
                    ]
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
                { display =
                    Pages.Projects.View.projectInfoColumns project
                        ++ [ { attributes = [ Style.classes.editable, Style.classes.numberCell ]
                             , children = []
                             }
                           ]
                , controls =
                    [ td [ Style.classes.controls ]
                        [ button [ Style.classes.button.select, onClick <| Pages.Util.Choice.Page.SelectChoice <| project ]
                            [ text <| .select <| .language <| main ]
                        ]
                    , td [ Style.classes.controls ]
                        [ NavigationUtil.projectEditorLinkButton configuration project.id main.language.projects ]
                    ]
                }
        }
        main


{-| Todo: The function is oddly specific, and the implementation with the fixed amount of columns is awkward,
especially because the non-matching case should never occur.
-}
projectInfoFromMap : DictList Types.Project.Id.Id (Editing Types.Project.Project.Project Types.DashboardEntry.Creation.ClientInput) -> Types.Project.Id.Id -> List (HtmlUtil.Column Page.LogicMsg)
projectInfoFromMap projects projectId =
    DictList.get projectId projects
        |> Maybe.Extra.unwrap (List.repeat 2 { attributes = [ Style.classes.editable ], children = [] }) (.original >> Pages.Projects.View.projectInfoColumns)
