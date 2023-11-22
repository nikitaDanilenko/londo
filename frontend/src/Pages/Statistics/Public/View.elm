module Pages.Statistics.Public.View exposing (view)

import Configuration exposing (Configuration)
import Html exposing (Html, h2, hr, section, table, tbody, td, text, tr)
import Html.Attributes exposing (colspan)
import Maybe.Extra
import Pages.Statistics.Page
import Pages.Statistics.Public.Page as Page
import Pages.Statistics.View
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.ParentEditor.View
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Types.Progress.Progress
import Util.DictList as DictList
import Util.SearchUtil as SearchUtil


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
        , id = Style.ids.statistics
        }
    <|
        Pages.Statistics.View.viewDashboardStatistics
            Page.SetViewType
            main.languages.statistics
            main.languages.dashboard
            main.viewType
            main.dashboard
            main.dashboardStatistics
            :: section []
                [ HtmlUtil.searchAreaWith
                    { searchString = main.searchString
                    , clearWord = main.languages.taskEditor.clearSearch
                    , msg = Page.SetSearchString
                    }
                ]
            :: (main.projects
                    |> DictList.values
                    |> List.map
                        (viewResolvedProject main.viewType main.languages.taskEditor main.languages.statistics main.searchString)
               )


viewResolvedProject : Pages.Statistics.Page.ViewType -> Page.TaskEditorLanguage -> Page.StatisticsLanguage -> String -> Page.ProjectAnalysis -> Html Page.LogicMsg
viewResolvedProject viewType taskEditorLanguage statisticsLanguage searchString projectAnalysis =
    let
        project =
            projectAnalysis.project

        projectName =
            project.name ++ (project.description |> Maybe.Extra.unwrap "" (\description -> " (" ++ description ++ ")"))

        ( finished, unfinished ) =
            projectAnalysis
                |> .tasks
                |> List.filter (.task >> .name >> SearchUtil.search searchString)
                |> List.partition (.task >> .progress >> Types.Progress.Progress.isComplete)

        display =
            List.sortBy (.task >> .name)
                >> List.indexedMap (\index -> viewTask (index + 1) viewType)
                >> List.concat

        headerColumns =
            Pages.Statistics.View.taskInfoHeaderColumns viewType taskEditorLanguage statisticsLanguage

        separator =
            if List.any List.isEmpty [ finished, unfinished ] then
                []

            else
                -- todo: Consider a better way of supplying the number of columns
                -- todo: A text hint may be a good idea.
                [ tr [] [ td [ colspan <| 12 ] [ hr [] [] ] ] ]
    in
    section []
        (h2 []
            [ text <| projectName ]
            :: [ table [ Style.classes.elementsWithControlsTable ]
                    [ Pages.Util.ParentEditor.View.tableHeaderWith
                        { columns = headerColumns
                        , style = Style.classes.taskEditTable
                        }
                    , tbody []
                        (List.concat
                            [ unfinished |> display
                            , separator
                            , finished |> display
                            ]
                        )
                    ]
               ]
        )


viewTask : Int -> Pages.Statistics.Page.ViewType -> Page.TaskAnalysis -> List (Html Page.LogicMsg)
viewTask index viewType taskAnalysis =
    [ tr [ Style.classes.editing ]
        (Pages.Statistics.View.taskInfoColumns index viewType taskAnalysis |> List.map (HtmlUtil.withExtraAttributes []))
    ]
