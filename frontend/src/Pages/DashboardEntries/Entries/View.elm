module Pages.DashboardEntries.Entries.View exposing (..)

import Configuration exposing (Configuration)
import Html exposing (Html, button, text, th)
import Html.Attributes exposing (disabled)
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
import Util.DictListUtil as DictListUtil
import Util.Editing exposing (Editing)
import Util.MaybeUtil as MaybeUtil
import Util.SearchUtil as SearchUtil


viewEntries : Configuration -> Page.Main -> Html Page.LogicMsg
viewEntries configuration main =
    Pages.Util.Choice.View.viewElements
        { header = main.language.dashboardEntries
        , nameOfChoice = .name
        , choiceIdOfElement = .projectId
        , idOfElement = .projectId
        , elementHeaderColumns = headerColumns main.language
        , info =
            \entry ->
                { display = projectInfoFromMap main.choices entry.projectId
                , controls =
                    [ button
                        [ Style.classes.button.delete, onClick <| Pages.Util.Choice.Page.RequestDelete <| entry.projectId ]
                        [ text <| .delete <| main.language ]
                    , NavigationUtil.projectEditorLinkButton configuration entry.projectId <| .taskEditor <| main.language
                    ]
                }
        , isValidInput = always True
        , edit = \_ _ -> []
        , clearSearchWord = main.language.clearSearch
        }
        main


viewProjects : Configuration -> Page.Main -> Html Page.LogicMsg
viewProjects configuration main =
    Pages.Util.Choice.View.viewChoices
        { header = main.language.projects
        , matchesSearchText = \string project -> SearchUtil.search string project.name || SearchUtil.search string (project.description |> Maybe.withDefault "")
        , sortBy = .name
        , choiceHeaderColumns = headerColumns main.language
        , idOfChoice = .id
        , elementCreationLine =
            \project creation ->
                let
                    validInput =
                        main.elements
                            |> DictList.get creation.projectId
                            |> Maybe.Extra.isNothing

                    addMsg =
                        Pages.Util.Choice.Page.Create project.id

                    cancelMsg =
                        Pages.Util.Choice.Page.DeselectChoice project.id

                    ( confirmName, confirmStyle ) =
                        if DictListUtil.existsValue (\choice -> choice.original.projectId == creation.projectId) main.elements then
                            ( main.language.added, Style.classes.disabled )

                        else
                            ( main.language.add, Style.classes.button.confirm )
                in
                { display =
                    Pages.Projects.View.projectInfoColumns project
                        ++ [ { attributes = [ Style.classes.numberLabel ]
                             , children =
                                []
                             }
                           ]
                , controls =
                    [ button
                        ([ MaybeUtil.defined <| confirmStyle
                         , MaybeUtil.defined <| disabled <| not <| validInput
                         , MaybeUtil.optional validInput <| onClick addMsg
                         ]
                            |> Maybe.Extra.values
                        )
                        [ text <| confirmName ]
                    , button
                        [ Style.classes.button.cancel, onClick <| cancelMsg ]
                        [ text <| .cancel <| .language <| main ]
                    ]
                }
        , viewChoiceLine =
            \project ->
                { display = Pages.Projects.View.projectInfoColumns project
                , controls =
                    [ button
                        [ Style.classes.button.select, onClick <| Pages.Util.Choice.Page.SelectChoice <| project ]
                        [ text <| .select <| .language <| main ]
                    , NavigationUtil.projectEditorLinkButton configuration project.id main.language.taskEditor
                    ]
                }
        , clearSearchWord = main.language.clearSearch
        }
        main


headerColumns : Page.Language -> List (Html msg)
headerColumns language =
    [ th [] [ text <| .dashboardEntryName <| language ]
    , th [] [ text <| .dashboardEntryDescription <| language ]
    ]


{-| Todo: The function is oddly specific, and the implementation with the fixed amount of columns is awkward,
especially because the non-matching case should never occur.
-}
projectInfoFromMap : DictList Types.Project.Id.Id (Editing Types.Project.Project.Project Types.DashboardEntry.Creation.ClientInput) -> Types.Project.Id.Id -> List (HtmlUtil.Column Page.LogicMsg)
projectInfoFromMap projects projectId =
    DictList.get projectId projects
        |> Maybe.Extra.unwrap (List.repeat 2 { attributes = [ Style.classes.editable ], children = [] }) (.original >> Pages.Projects.View.projectInfoColumns)
