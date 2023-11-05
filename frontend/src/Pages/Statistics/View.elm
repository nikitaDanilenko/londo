module Pages.Statistics.View exposing (view)

import Basics.Extra exposing (flip)
import BigInt exposing (BigInt)
import BigRational exposing (BigRational)
import Configuration exposing (Configuration)
import Html exposing (Html, button, div, h1, h2, hr, input, nav, section, table, tbody, td, text, th, thead, tr)
import Html.Attributes exposing (checked, colspan, disabled, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import List.Extra
import LondoGQL.Enum.TaskKind as TaskKind
import Math.Natural as Natural
import Math.Statistics
import Maybe.Extra
import Monocle.Compose as Compose
import Monocle.Lens as Lens
import Pages.Dashboards.View
import Pages.Statistics.EditingResolvedProject exposing (EditingResolvedProject)
import Pages.Statistics.Page as Page
import Pages.Statistics.Pagination as Pagination exposing (Pagination)
import Pages.Tasks.Tasks.View
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.PaginationSettings as PaginationSettings
import Pages.Util.ParentEditor.View
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Paginate
import Types.Progress.Progress
import Types.Simulation.Update
import Types.Task.TaskWithSimulation
import Types.Task.Update
import Util.DictList as DictList exposing (DictList)
import Util.Editing as Editing
import Util.LensUtil as LensUtil
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
        viewDashboard
            main.languages.statistics
            main.languages.dashboard
            main.dashboard
            main.dashboardStatistics
            :: (main.projects
                    |> DictList.values
                    |> List.map
                        (viewResolvedProject main.languages.taskEditor main.languages.statistics main.pagination)
               )


rationalToString : BigRational -> String
rationalToString =
    BigRational.toDecimalString Page.numberOfDecimalPlaces


viewDashboard : Page.StatisticsLanguage -> Page.DashboardLanguage -> Page.Dashboard -> Page.DashboardStatistics -> Html Page.LogicMsg
viewDashboard statisticsLanguage dashboardLanguage dashboard statistics =
    section []
        [ table [ Style.classes.elementsWithControlsTable ]
            [ Pages.Dashboards.View.tableHeader dashboardLanguage
            , tr [ Style.classes.statisticsLine ]
                (Pages.Dashboards.View.dashboardInfoColumns dashboard
                    |> List.map (HtmlUtil.withExtraAttributes [])
                )
            ]
        , h1 [] [ text <| statisticsLanguage.statistics ]
        , table [ Style.classes.elementsWithControlsTable ]
            [ thead []
                [ tr []
                    [ th [] [ text <| "" ]
                    , th [] [ text <| .total <| statisticsLanguage ]
                    , th [] [ text <| .counted <| statisticsLanguage ]
                    , th [] [ text <| .simulatedTotal <| statisticsLanguage ]
                    , th [] [ text <| .simulatedCounted <| statisticsLanguage ]
                    ]
                ]
            , tbody []
                --todo: reachableAll, and reachedAll are only meaningful for non-percent values,
                -- because otherwise the values are misleading. Adjust that.
                [ tr [ Style.classes.editing, Style.classes.statisticsLine ]
                    [ td [] [ text <| .reachedAll <| statisticsLanguage ]
                    , td []
                        [ text <| Natural.toString <| .total <| .reached <| statistics
                        ]
                    , td []
                        [ text <| Natural.toString <| .counted <| .reached <| statistics
                        ]
                    , td [] [ text <| Natural.toString <| .simulatedTotal <| .reached <| statistics ]
                    , td [] [ text <| Natural.toString <| .simulatedCounted <| .reached <| statistics ]
                    ]
                , tr [ Style.classes.editing, Style.classes.statisticsLine ]
                    [ td [] [ text <| .reachableAll <| statisticsLanguage ]
                    , td []
                        [ text <| Natural.toString <| .total <| .reachable <| statistics
                        ]
                    , td []
                        [ text <| Natural.toString <| .counted <| .reachable <| statistics
                        ]
                    , td [] []
                    , td [] []
                    ]
                , tr [ Style.classes.editing, Style.classes.statisticsLine ]
                    [ td [] [ text <| .meanAbsolute <| statisticsLanguage ]
                    , td [] [ text <| BigRational.toDecimalString Page.numberOfDecimalPlaces <| .total <| .absoluteMeans <| statistics ]
                    , td [] [ text <| BigRational.toDecimalString Page.numberOfDecimalPlaces <| .counted <| .absoluteMeans <| statistics ]
                    , td [] [ text <| BigRational.toDecimalString Page.numberOfDecimalPlaces <| .simulatedTotal <| .absoluteMeans <| statistics ]
                    , td [] [ text <| BigRational.toDecimalString Page.numberOfDecimalPlaces <| .simulatedCounted <| .absoluteMeans <| statistics ]
                    ]
                , tr [ Style.classes.editing, Style.classes.statisticsLine ]
                    [ td [] [ text <| .meanRelative <| statisticsLanguage ]
                    , td [] [ text <| BigRational.toDecimalString Page.numberOfDecimalPlaces <| .total <| .relativeMeans <| statistics ]
                    , td [] [ text <| BigRational.toDecimalString Page.numberOfDecimalPlaces <| .counted <| .relativeMeans <| statistics ]
                    , td [] [ text <| BigRational.toDecimalString Page.numberOfDecimalPlaces <| .simulatedTotal <| .relativeMeans <| statistics ]
                    , td [] [ text <| BigRational.toDecimalString Page.numberOfDecimalPlaces <| .simulatedCounted <| .relativeMeans <| statistics ]
                    ]
                ]
            ]
        ]


viewResolvedProject : Page.TaskEditorLanguage -> Page.StatisticsLanguage -> Pagination -> EditingResolvedProject -> Html Page.LogicMsg
viewResolvedProject taskEditorLanguage statisticsLanguage pagination resolvedProject =
    let
        project =
            resolvedProject.project

        projectName =
            project.name ++ (project.description |> Maybe.Extra.unwrap "" (\description -> " (" ++ description ++ ")"))

        tasks =
            resolvedProject
                |> .tasks
                |> DictList.values

        ( finished, unfinished ) =
            tasks |> List.partition (.original >> .task >> .progress >> Types.Progress.Progress.isComplete)

        paginationSettingsLens taskStatus =
            (case taskStatus of
                Page.Finished ->
                    Pagination.lenses.finishedTasks

                Page.Unfinished ->
                    Pagination.lenses.unfinishedTasks
            )
                |> Compose.lensWithLens (LensUtil.dictByKeyWithDefault project.id PaginationSettings.initial)

        paginate taskStatus =
            List.sortBy (.original >> .task >> .name >> String.toLower)
                >> ViewUtil.paginate
                    { pagination = (taskStatus |> paginationSettingsLens).get
                    }
                    pagination

        unfinishedPaginated =
            unfinished |> paginate Page.Unfinished

        finishedPaginated =
            finished |> paginate Page.Finished

        pager taskStatus elements =
            let
                lens =
                    paginationSettingsLens taskStatus
            in
            nav [ Style.classes.pagination ]
                [ ViewUtil.pagerButtons
                    { msg =
                        PaginationSettings.updateCurrentPage
                            { pagination = LensUtil.identityLens
                            , items = lens
                            }
                            pagination
                            >> lens.get
                            >> Page.SetProjectPagination project.id taskStatus
                    , elements = elements
                    }
                ]

        taskNumbers =
            { numberOfAllTasks = tasks |> List.length
            , numberOfCountedTasks = tasks |> List.Extra.count (.original >> .task >> .counting)
            }

        display =
            Paginate.page
                >> List.concatMap
                    (Editing.unpack
                        { onView = viewTask project.id taskEditorLanguage taskNumbers
                        , onUpdate = .task >> updateTask taskEditorLanguage project.id
                        , onDelete = \_ -> []
                        }
                    )

        separator =
            if List.any List.isEmpty [ finished, unfinished ] then
                []

            else
                -- todo: Consider a better way of supplying the number of columns
                -- todo: A text hint may be a good idea.
                [ tr [] [ td [ colspan <| 11 ] [ hr [] [] ] ] ]
    in
    section []
        (h2 []
            [ text <| projectName ]
            :: [ table [ Style.classes.elementsWithControlsTable ]
                    [ taskInfoHeader taskEditorLanguage statisticsLanguage
                    , tbody []
                        (List.concat
                            [ unfinishedPaginated |> display
                            , [ unfinishedPaginated |> pager Page.Unfinished ]
                            , separator
                            , finishedPaginated |> display
                            , [ finishedPaginated |> pager Page.Finished ]
                            ]
                        )
                    ]
               ]
        )



-- todo: Adjust columns


taskInfoHeader : Page.TaskEditorLanguage -> Page.StatisticsLanguage -> Html msg
taskInfoHeader taskEditorLanguage statisticsLanguage =
    Pages.Util.ParentEditor.View.tableHeaderWith
        { columns =
            [ th [] [ text <| .taskName <| taskEditorLanguage ]
            , th [] [ text <| .taskKind <| taskEditorLanguage ]
            , th [] [ text <| .progress <| taskEditorLanguage ]
            , th [] [ text <| .simulation <| statisticsLanguage ]
            , th [] [ text <| .unit <| taskEditorLanguage ]
            , th [] [ text <| .counting <| taskEditorLanguage ]
            , th [] [ text <| .mean <| statisticsLanguage ]
            , th [] [ text <| .differenceOneTotal <| statisticsLanguage ]
            , th [] [ text <| .differenceOneCounted <| statisticsLanguage ]
            , th [] [ text <| .differenceCompleteTotal <| statisticsLanguage ]
            , th [] [ text <| .differenceCompleteCounted <| statisticsLanguage ]
            ]
        , style = Style.classes.taskEditTable
        }


type alias TaskNumbers =
    { numberOfAllTasks : Int
    , numberOfCountedTasks : Int
    }


taskInfoColumns :
    TaskNumbers
    -> Page.ResolvedTask
    -> List (HtmlUtil.Column msg)
taskInfoColumns ps resolvedTask =
    let
        progress =
            resolvedTask.task.progress

        mean =
            progress
                |> Types.Progress.Progress.toPercentRational
                |> rationalToString

        differenceAfterOneMoreExactTotal =
            Math.Statistics.differenceAfterOneMoreExact
                { numberOfElements = ps.numberOfAllTasks }
                progress

        differenceAfterOneMoreExactCounted =
            Math.Statistics.differenceAfterOneMoreExact
                { numberOfElements = ps.numberOfCountedTasks }
                progress

        afterCompletionExactTotal =
            Math.Statistics.differenceAfterCompletionExact
                { numberOfElements = ps.numberOfAllTasks }
                progress

        afterCompletionExactCounted =
            Math.Statistics.differenceAfterCompletionExact
                { numberOfElements = ps.numberOfCountedTasks }
                progress
    in
    [ { attributes = [ Style.classes.editable ]
      , children = [ text <| .name <| .task <| resolvedTask ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| TaskKind.toString <| .taskKind <| .task <| resolvedTask ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ Pages.Tasks.Tasks.View.displayProgress resolvedTask.task.progress resolvedTask.task.taskKind ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.Extra.unwrap "" (.reachedModifier >> BigInt.toString) <| .simulation <| resolvedTask ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.withDefault "" <| .unit <| .task <| resolvedTask ]
      }
    , { attributes = []
      , children = [ input [ type_ "checkbox", checked <| .counting <| .task <| resolvedTask, disabled True ] [] ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| mean ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.Extra.unwrap "" rationalToString <| differenceAfterOneMoreExactTotal ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.Extra.unwrap "" rationalToString <| differenceAfterOneMoreExactCounted ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.Extra.unwrap "" rationalToString <| afterCompletionExactTotal ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.Extra.unwrap "" rationalToString <| afterCompletionExactCounted ]
      }
    ]


viewTask : Page.ProjectId -> Page.TaskEditorLanguage -> TaskNumbers -> Page.ResolvedTask -> Bool -> List (Html Page.LogicMsg)
viewTask projectId language taskNumbers resolvedTask showControls =
    Pages.Util.ParentEditor.View.lineWith
        { rowWithControls =
            \t ->
                { display = taskInfoColumns taskNumbers t
                , controls =
                    [ div []
                        [ button
                            [ Style.classes.button.edit, onClick <| Page.EnterEditTask projectId <| .id <| .task <| t ]
                            [ text <| .edit <| language ]
                        ]
                    ]
                }
        , toggleMsg = Page.ToggleControls projectId resolvedTask.task.id
        , showControls = showControls
        }
        resolvedTask


updateTask : Page.TaskEditorLanguage -> Page.ProjectId -> Page.Task -> Page.TaskUpdate -> List (Html Page.LogicMsg)
updateTask language projectId task update =
    let
        progressUpdateLens =
            Types.Task.TaskWithSimulation.lenses.taskUpdate
                |> Compose.lensWithLens
                    Types.Task.Update.lenses.progressUpdate

        reachedModifierLens =
            Types.Task.TaskWithSimulation.lenses.simulation
                |> Compose.lensWithLens Types.Simulation.Update.lenses.reachedModifier

        validReached =
            update
                |> progressUpdateLens.get
                |> .reached
                |> ValidatedInput.isValid

        validReachable =
            update
                |> progressUpdateLens.get
                |> .reachable
                |> ValidatedInput.isValid

        validSimulation =
            update
                |> reachedModifierLens.get
                |> ValidatedInput.isValid

        validInput =
            validReached && validReachable && validSimulation

        validatedSaveAction =
            MaybeUtil.optional validInput <| onEnter <| Page.SaveEditTask projectId <| .id <| task

        updateMsg =
            Page.EditTask projectId task.id

        cancelMsg =
            Page.ExitEditTask projectId <| .id <| task

        saveMsg =
            Page.SaveEditTask projectId <| .id <| task

        filler =
            List.repeat 5 <| td [] []

        --todo: Consider a more stable way of computing the number of values
        infoColumns =
            [ td [ Style.classes.editable ]
                [ text <| .name <| task ]
            , td []
                [ text <| TaskKind.toString <| .taskKind <| task ]
            , td []
                (Pages.Tasks.Tasks.View.editProgress
                    { progressLens = progressUpdateLens
                    , updateMsg = updateMsg
                    }
                    (update |> (Types.Task.TaskWithSimulation.lenses.taskUpdate |> Compose.lensWithLens Types.Task.Update.lenses.taskKind).get)
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
                [ input
                    ([ MaybeUtil.defined <| value <| .text <| reachedModifierLens.get <| update
                     , MaybeUtil.defined <|
                        onInput <|
                            flip
                                (ValidatedInput.lift reachedModifierLens).set
                                update
                                >> updateMsg
                     , MaybeUtil.defined <| HtmlUtil.onEscape <| cancelMsg
                     , validatedSaveAction
                     ]
                        |> Maybe.Extra.values
                    )
                    []
                ]
            , td [ Style.classes.editable ]
                [ text <| Maybe.withDefault "" <| task.unit ]
            , td []
                [ input
                    [ type_ "checkbox"
                    , checked <| (Types.Task.TaskWithSimulation.lenses.taskUpdate |> Compose.lensWithLens Types.Task.Update.lenses.counting).get <| update
                    , onClick <|
                        updateMsg <|
                            Lens.modify (Types.Task.TaskWithSimulation.lenses.taskUpdate |> Compose.lensWithLens Types.Task.Update.lenses.counting) not <|
                                update
                    , onEnter <| saveMsg
                    , HtmlUtil.onEscape cancelMsg
                    ]
                    []
                ]
            ]
                ++ filler

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
