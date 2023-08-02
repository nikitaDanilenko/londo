module Pages.Statistics.View exposing (view)

import Configuration exposing (Configuration)
import Html exposing (Html, button, h3, input, section, table, td, text, th, thead, tr)
import Html.Attributes exposing (checked, disabled, type_)
import Html.Events exposing (onClick)
import LondoGQL.Enum.TaskKind as TaskKind
import Maybe.Extra
import Pages.Dashboards.View
import Pages.Statistics.EditingResolvedProject exposing (EditingResolvedProject)
import Pages.Statistics.Page as Page
import Pages.Tasks.Tasks.View
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.ParentEditor.View
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Util.DictList as DictList
import Util.Editing as Editing


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
            (viewDashboard main.languages.statistics main.languages.dashboard main.dashboard
                :: (main.projects
                        |> DictList.values
                        |> List.map
                            (viewResolvedProject configuration main.languages.taskEditor)
                   )
            )


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


viewResolvedProject : Configuration -> Page.TaskEditorLanguage -> EditingResolvedProject -> Html Page.LogicMsg
viewResolvedProject configuration taskEditorLanguage resolvedProject =
    let
        project =
            resolvedProject.project

        projectName =
            project.name ++ (project.description |> Maybe.Extra.unwrap "" (\description -> " (" ++ description ++ ")"))
    in
    section []
        (h3 []
            [ text <| projectName ]
            :: (resolvedProject
                    |> .tasks
                    |> DictList.values
                    |> List.concatMap
                        (\e ->
                            Editing.unpack
                                { onView =
                                    \editingTask showControls ->
                                        Pages.Util.ParentEditor.View.lineWith
                                            { rowWithControls =
                                                \task ->
                                                    { display = taskInfoColumns task
                                                    , controls =
                                                        [ td [ Style.classes.controls ]
                                                            [ button
                                                                [ Style.classes.button.edit, onClick <| Page.EnterEditTask project.id <| task.id ]
                                                                [ text <| .edit <| taskEditorLanguage ]
                                                            ]
                                                        ]
                                                    }
                                            , toggleMsg = Page.ToggleControls project.id e.original.id
                                            , showControls = showControls
                                            }
                                            editingTask
                                , onUpdate = \_ _ -> []
                                , onDelete = \_ -> []
                                }
                                e
                        )
               )
        )



-- todo: Adjust columns


taskInfoColumns : Page.Task -> List (HtmlUtil.Column msg)
taskInfoColumns task =
    [ { attributes = [ Style.classes.editable ]
      , children = [ text task.name ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| TaskKind.toString <| task.taskKind ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ Pages.Tasks.Tasks.View.displayProgress task.progress task.taskKind ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.withDefault "" <| task.unit ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ input [ type_ "checkbox", checked <| task.counting, disabled True ] [] ]
      }
    ]


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
