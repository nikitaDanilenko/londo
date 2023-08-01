module Pages.Statistics.View exposing (view)

import Configuration exposing (Configuration)
import Html exposing (Html, button, h1, section, table, td, text, th, thead, tr)
import Html.Events exposing (onClick)
import Pages.Dashboards.View
import Pages.Statistics.Page as Page
import Pages.Tasks.Tasks.View
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate


view : Page.Model -> Html Page.Msg
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Configuration -> Page.Main -> Html Page.LogicMsg
viewMain configuration main =
    ViewUtil.viewMainWith
        { configuration = configuration
        , currentPage = Nothing
        , showNavigation = True
        }
    <|
        section []
            [ viewDashboard main.languages.statistics main.languages.dashboard main.dashboard
            ]


viewDashboard : Page.StatisticsLanguage -> Page.DashboardLanguage -> Page.Dashboard -> Html Page.LogicMsg
viewDashboard statisticsLanguage dashboardLanguage dashboard =
    section []
        [ table []
            [ Pages.Dashboards.View.tableHeader dashboardLanguage
            , tr []
                (Pages.Dashboards.View.dashboardInfoColumns dashboard
                    |> List.map (HtmlUtil.withExtraAttributes [])
                )
            ]
        , table []
            [ thead []
                [ tr []
                    [ th [] [ text <| "" ]
                    , th [] [ text <| .total <| statisticsLanguage ]
                    , th [] [ text <| .counted <| statisticsLanguage ]
                    , th [] [ text <| .simulated <| statisticsLanguage ]
                    ]
                ]
            ]
        ]



--viewProject : Configuration -> Page.ProjectLanguage -> Page.Project -> Bool -> List (Html Page.LogicMsg)
--viewProject configuration language project showControls =
--    Pages.Projects.View.projectLineWith
--        { controls =
--            [ td [ Style.classes.controls ]
--                [ NavigationUtil.projectEditorLinkButton configuration project.id language.taskEditor
--                ]
--            ]
--        , toggleMsg = Pages.Util.ParentEditor.Page.ToggleControls project.id
--        , showControls = showControls
--        }
--        project


viewTask : Page.ProjectId -> Page.TaskEditorLanguage -> Page.Task -> Bool -> List (Html Page.LogicMsg)
viewTask projectId language task showControls =
    Pages.Tasks.Tasks.View.taskLineWith
        { controls =
            [ td [ Style.classes.controls ]
                [ button
                    [ Style.classes.button.edit, onClick <| Page.EnterEditTask projectId <| task.id ]
                    [ text <| language.edit ]
                ]
            ]
        , toggleMsg = Page.ToggleControls projectId <| task.id
        , showControls = showControls
        }
        task
