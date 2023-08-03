module Pages.Statistics.View exposing (view)

import BigInt exposing (BigInt)
import Configuration exposing (Configuration)
import Html exposing (Html, button, h3, input, section, table, tbody, td, text, th, thead, tr)
import Html.Attributes exposing (checked, disabled, type_)
import Html.Events exposing (onClick)
import Html.Events.Extra exposing (onEnter)
import LondoGQL.Enum.TaskKind as TaskKind
import Math.Natural as Natural
import Math.Positive as Positive
import Maybe.Extra
import Monocle.Lens as Lens
import Pages.Dashboards.View
import Pages.Statistics.EditingResolvedProject as EditingResolvedProject exposing (EditingResolvedProject)
import Pages.Statistics.Page as Page
import Pages.Tasks.Tasks.View
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.ParentEditor.View
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Types.Task.Update
import Util.DictList as DictList
import Util.Editing as Editing
import Util.MaybeUtil as MaybeUtil
import Util.ValidatedInput as ValidatedInput


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
        let
            groupedTasks =
                main.projects
                    |> DictList.values
                    |> List.map EditingResolvedProject.tasks
        in
        section []
            (viewDashboard main.languages.statistics
                main.languages.dashboard
                main.dashboard
                groupedTasks
                :: (main.projects
                        |> DictList.values
                        |> List.map
                            (viewResolvedProject main.languages.taskEditor)
                   )
            )


viewDashboard : Page.StatisticsLanguage -> Page.DashboardLanguage -> Page.Dashboard -> List (List Page.Task) -> Html Page.LogicMsg
viewDashboard statisticsLanguage dashboardLanguage dashboard resolvedProjects =
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
            , tbody []
                [ tr []
                    [ td [] [ text <| .reachedAll <| statisticsLanguage ]
                    , td []
                        [ text <|
                            BigInt.toString <|
                                List.foldl BigInt.add (BigInt.fromInt 0) <|
                                    List.map (reachedInProject { countedOnly = False }) <|
                                        resolvedProjects
                        ]
                    , td []
                        [ text <|
                            BigInt.toString <|
                                List.foldl BigInt.add (BigInt.fromInt 0) <|
                                    List.map (reachedInProject { countedOnly = True }) <|
                                        resolvedProjects
                        ]
                    , td [] [ text <| "tba" ] -- todo: Add simulation
                    ]
                , tr []
                    [ td [] [ text <| .reachableAll <| statisticsLanguage ]
                    , td []
                        [ text <|
                            BigInt.toString <|
                                List.foldl BigInt.add (BigInt.fromInt 0) <|
                                    List.map (reachableInProject { countedOnly = False }) <|
                                        resolvedProjects
                        ]
                    , td []
                        [ text <|
                            BigInt.toString <|
                                List.foldl BigInt.add (BigInt.fromInt 0) <|
                                    List.map (reachableInProject { countedOnly = True }) <|
                                        resolvedProjects
                        ]
                    , td [] [ text <| "tba" ] -- todo: Add simulation
                    ]
                ]
            ]
        ]


reachableInProject : { countedOnly : Bool } -> List Page.Task -> BigInt
reachableInProject ps ts =
    let
        predicate =
            if ps.countedOnly then
                .counting

            else
                always True
    in
    ts
        |> List.filterMap
            (Just
                >> Maybe.Extra.filter predicate
                >> Maybe.map (.progress >> .reachable)
            )
        |> Positive.sum


reachedInProject : { countedOnly : Bool } -> List Page.Task -> BigInt
reachedInProject ps ts =
    let
        predicate =
            if ps.countedOnly then
                .counting

            else
                always True
    in
    ts
        |> List.filterMap
            (Just
                >> Maybe.Extra.filter predicate
                >> Maybe.map (.progress >> .reached)
            )
        |> Natural.sum


viewResolvedProject : Page.TaskEditorLanguage -> EditingResolvedProject -> Html Page.LogicMsg
viewResolvedProject taskEditorLanguage resolvedProject =
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
                    |> List.sortBy (.original >> .name)
                    -- todo: Use progress sorting
                    |> List.concatMap
                        (Editing.unpack
                            { onView = viewTask project.id taskEditorLanguage
                            , onUpdate = updateTask taskEditorLanguage project.id
                            , onDelete = \_ -> []
                            }
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
    Pages.Util.ParentEditor.View.lineWith
        { rowWithControls =
            \t ->
                { display = taskInfoColumns t
                , controls =
                    [ td [ Style.classes.controls ]
                        [ button
                            [ Style.classes.button.edit, onClick <| Page.EnterEditTask projectId <| .id <| t ]
                            [ text <| .edit <| language ]
                        ]
                    ]
                }
        , toggleMsg = Page.ToggleControls projectId task.id
        , showControls = showControls
        }
        task


updateTask : Page.TaskEditorLanguage -> Page.ProjectId -> Page.Task -> Page.TaskUpdate -> List (Html Page.LogicMsg)
updateTask language projectId task update =
    let
        validInput =
            update
                |> Types.Task.Update.lenses.progressUpdate.get
                |> .reached
                |> ValidatedInput.isValid

        validatedSaveAction =
            MaybeUtil.optional validInput <| onEnter <| Page.SaveEditTask projectId <| .id <| task

        updateMsg =
            Page.EditTask projectId task.id

        cancelMsg =
            Page.ExitEditTask projectId <| .id <| task

        saveMsg =
            Page.SaveEditTask projectId <| .id <| task

        infoColumns =
            [ td [ Style.classes.editable ]
                [ text <| .name <| task ]
            , td []
                [ text <| TaskKind.toString <| .taskKind <| task ]
            , td []
                (Pages.Tasks.Tasks.View.editProgress
                    { progressLens = Types.Task.Update.lenses.progressUpdate
                    , updateMsg = updateMsg
                    }
                    (update |> Types.Task.Update.lenses.taskKind.get)
                    update
                    |> List.map
                        (HtmlUtil.withAttributes
                            ([ MaybeUtil.defined <| HtmlUtil.onEscape <| cancelMsg
                             , validatedSaveAction
                             ]
                                |> Maybe.Extra.values
                            )
                        )
                )
            , td [ Style.classes.editable ]
                [ text <| Maybe.withDefault "" <| task.unit ]
            , td []
                [ input
                    [ type_ "checkbox"
                    , checked <| Types.Task.Update.lenses.counting.get <| update
                    , onClick <|
                        updateMsg <|
                            Lens.modify Types.Task.Update.lenses.counting not <|
                                update
                    , onEnter <| saveMsg
                    , HtmlUtil.onEscape cancelMsg
                    ]
                    []
                ]
            ]

        controlsRow =
            Pages.Util.ParentEditor.View.controlsRowWith
                { colspan = infoColumns |> List.length
                , validInput = validInput
                , confirm =
                    { msg = saveMsg
                    , name = language |> .save
                    }
                , cancel =
                    { msg = cancelMsg
                    , name = language |> .cancel
                    }
                }
    in
    [ tr [ Style.classes.editLine ] (infoColumns ++ [ HtmlUtil.toggleControlsCell <| Page.ToggleControls projectId <| .id <| task ])
    , controlsRow
    ]
