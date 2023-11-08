module Pages.Statistics.View exposing (view)

import Basics.Extra exposing (flip)
import BigInt exposing (BigInt)
import Configuration exposing (Configuration)
import Html exposing (Html, button, div, h1, h2, hr, input, p, section, table, tbody, td, text, th, thead, tr)
import Html.Attributes exposing (checked, colspan, disabled, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import LondoGQL.Enum.TaskKind as TaskKind
import LondoGQL.Scalar
import Math.Natural
import Maybe.Extra
import Monocle.Compose as Compose
import Monocle.Lens as Lens
import Pages.Dashboards.View
import Pages.Statistics.EditingResolvedProject exposing (EditingResolvedProject)
import Pages.Statistics.Page as Page
import Pages.Tasks.Tasks.View
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.ParentEditor.View
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Types.Dashboard.WithSimulation
import Types.Dashboard.WithoutSimulation
import Types.Progress.Progress
import Types.Simulation.Update
import Types.Task.TaskWithSimulation
import Types.Task.Update
import Util.DictList as DictList exposing (DictList)
import Util.Editing as Editing
import Util.MaybeUtil as MaybeUtil
import Util.SearchUtil as SearchUtil
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
        viewDashboardStatistics
            main.languages.statistics
            main.languages.dashboard
            main.viewType
            main.dashboard
            main.dashboardStatistics
            :: section [] [ viewTaskSearchField main.searchString ]
            :: (main.projects
                    |> DictList.values
                    |> List.map
                        (viewResolvedProject main.viewType main.languages.taskEditor main.languages.statistics main.searchString)
               )


bigDecimalToString : LondoGQL.Scalar.BigDecimal -> String
bigDecimalToString (LondoGQL.Scalar.BigDecimal string) =
    string


viewTaskSearchField : String -> Html Page.LogicMsg
viewTaskSearchField searchString =
    HtmlUtil.searchAreaWith { msg = Page.SetSearchString, searchString = searchString }


viewTypeButtonWith :
    { label : String
    , currentViewType : Page.ViewType
    , forViewType : Page.ViewType
    }
    -> Html Page.LogicMsg
viewTypeButtonWith ps =
    button
        [ disabled <| ps.currentViewType == ps.forViewType
        , onClick <| Page.SetViewType ps.forViewType
        , Style.classes.button.navigation
        ]
        [ text <| ps.label
        ]


viewDashboardStatistics : Page.StatisticsLanguage -> Page.DashboardLanguage -> Page.ViewType -> Page.Dashboard -> Page.DashboardStatistics -> Html Page.LogicMsg
viewDashboardStatistics statisticsLanguage dashboardLanguage viewType dashboard statistics =
    section []
        [ table [ Style.classes.elementsWithControlsTable ]
            [ Pages.Dashboards.View.tableHeader dashboardLanguage
            , tr [ Style.classes.statisticsLine ]
                (Pages.Dashboards.View.dashboardInfoColumns dashboard
                    |> List.map (HtmlUtil.withExtraAttributes [])
                )
            ]
        , h1 [] [ text <| statisticsLanguage.statistics ]
        , p []
            [ viewTypeButtonWith
                { label = statisticsLanguage.total
                , currentViewType = viewType
                , forViewType = Page.Total
                }
            , viewTypeButtonWith
                { label = statisticsLanguage.counting
                , currentViewType = viewType
                , forViewType = Page.Counting
                }
            ]
        , table [ Style.classes.elementsWithControlsTable ]
            [ (case viewType of
                Page.Total ->
                    viewDashboardStatisticsWith
                        { headerActual = .total
                        , headerSimulated = .simulatedTotal
                        , reachedActual = .total
                        , reachedSimulated = .simulatedTotal
                        , reachable = .total
                        , meanAbsoluteActual = .total
                        , meanAbsoluteSimulated = .simulatedTotal
                        , meanRelativeActual = .total
                        , meanRelativeSimulated = .simulatedTotal
                        }

                Page.Counting ->
                    viewDashboardStatisticsWith
                        { headerActual = .counting
                        , headerSimulated = .simulatedCounting
                        , reachedActual = .counting
                        , reachedSimulated = .simulatedCounting
                        , reachable = .counting
                        , meanAbsoluteActual = .counting
                        , meanAbsoluteSimulated = .simulatedCounting
                        , meanRelativeActual = .counting
                        , meanRelativeSimulated = .simulatedCounting
                        }
              )
                statisticsLanguage
                statistics
            ]
        ]


viewDashboardStatisticsWith :
    { headerActual : Page.StatisticsLanguage -> String
    , headerSimulated : Page.StatisticsLanguage -> String
    , reachedActual : Types.Dashboard.WithSimulation.WithSimulation Math.Natural.Natural -> Math.Natural.Natural
    , reachedSimulated : Types.Dashboard.WithSimulation.WithSimulation Math.Natural.Natural -> Math.Natural.Natural
    , reachable : Types.Dashboard.WithoutSimulation.WithoutSimulation -> Math.Natural.Natural
    , meanAbsoluteActual : Types.Dashboard.WithSimulation.WithSimulation LondoGQL.Scalar.BigDecimal -> LondoGQL.Scalar.BigDecimal
    , meanAbsoluteSimulated : Types.Dashboard.WithSimulation.WithSimulation LondoGQL.Scalar.BigDecimal -> LondoGQL.Scalar.BigDecimal
    , meanRelativeActual : Types.Dashboard.WithSimulation.WithSimulation LondoGQL.Scalar.BigDecimal -> LondoGQL.Scalar.BigDecimal
    , meanRelativeSimulated : Types.Dashboard.WithSimulation.WithSimulation LondoGQL.Scalar.BigDecimal -> LondoGQL.Scalar.BigDecimal
    }
    -> Page.StatisticsLanguage
    -> Page.DashboardStatistics
    -> Html Page.LogicMsg
viewDashboardStatisticsWith ps statisticsLanguage statistics =
    table
        [ Style.classes.elementsWithControlsTable ]
        [ thead []
            [ tr []
                [ th [] [ text <| "" ]
                , th [] [ text <| ps.headerActual <| statisticsLanguage ]
                , th [] [ text <| ps.headerSimulated <| statisticsLanguage ]
                ]
            ]
        , tbody []
            --todo: reachableAll, and reachedAll are only meaningful for non-percent values,
            -- because otherwise the values are misleading. Adjust that.
            [ tr [ Style.classes.editing, Style.classes.statisticsLine ]
                [ td [] [ text <| .reachedAll <| statisticsLanguage ]
                , td []
                    [ text <| Math.Natural.toString <| ps.reachedActual <| .reached <| statistics
                    ]
                , td [] [ text <| Math.Natural.toString <| ps.reachedSimulated <| .reached <| statistics ]
                ]
            , tr [ Style.classes.editing, Style.classes.statisticsLine ]
                [ td [] [ text <| .reachableAll <| statisticsLanguage ]
                , td []
                    [ text <| Math.Natural.toString <| ps.reachable <| .reachable <| statistics
                    ]
                , td []
                    [ text <| Math.Natural.toString <| .counting <| .reachable <| statistics
                    ]
                ]
            , tr [ Style.classes.editing, Style.classes.statisticsLine ]
                [ td [] [ text <| .meanAbsolute <| statisticsLanguage ]
                , td [] [ text <| bigDecimalToString <| ps.meanAbsoluteActual <| .absoluteMeans <| statistics ]
                , td [] [ text <| bigDecimalToString <| ps.meanAbsoluteSimulated <| .absoluteMeans <| statistics ]
                ]
            , tr [ Style.classes.editing, Style.classes.statisticsLine ]
                [ td [] [ text <| .meanRelative <| statisticsLanguage ]
                , td [] [ text <| bigDecimalToString <| ps.meanRelativeActual <| .relativeMeans <| statistics ]
                , td [] [ text <| bigDecimalToString <| ps.meanRelativeSimulated <| .relativeMeans <| statistics ]
                ]
            ]
        ]


viewResolvedProject : Page.ViewType -> Page.TaskEditorLanguage -> Page.StatisticsLanguage -> String -> EditingResolvedProject -> Html Page.LogicMsg
viewResolvedProject viewType taskEditorLanguage statisticsLanguage searchString resolvedProject =
    let
        project =
            resolvedProject.project

        projectName =
            project.name ++ (project.description |> Maybe.Extra.unwrap "" (\description -> " (" ++ description ++ ")"))

        tasks =
            resolvedProject
                |> .tasks
                |> DictList.values
                |> List.filter
                    (.original >> .task >> .name >> SearchUtil.search searchString)

        ( finished, unfinished ) =
            tasks |> List.partition (.original >> .task >> .progress >> Types.Progress.Progress.isComplete)

        display =
            List.sortBy (.original >> .task >> .name)
                >> List.indexedMap
                    (\index ->
                        let
                            position =
                                index + 1
                        in
                        Editing.unpack
                            { onView = viewTask position viewType project.id taskEditorLanguage
                            , onUpdate = .task >> updateTask position taskEditorLanguage project.id
                            , onDelete = \_ -> []
                            }
                    )
                >> List.concat

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
                    [ taskInfoHeader viewType taskEditorLanguage statisticsLanguage
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



-- todo: Adjust columns


taskInfoHeader : Page.ViewType -> Page.TaskEditorLanguage -> Page.StatisticsLanguage -> Html msg
taskInfoHeader viewType taskEditorLanguage statisticsLanguage =
    let
        deltaColumns =
            case viewType of
                Page.Total ->
                    [ th [] [ text <| .differenceOneTotal <| statisticsLanguage ]
                    , th [] [ text <| .differenceCompleteTotal <| statisticsLanguage ]
                    ]

                Page.Counting ->
                    [ th [] [ text <| .differenceOneCounting <| statisticsLanguage ]
                    , th [] [ text <| .differenceCompleteCounting <| statisticsLanguage ]
                    ]
    in
    Pages.Util.ParentEditor.View.tableHeaderWith
        { columns =
            [ th [] []
            , th [] [ text <| .taskName <| taskEditorLanguage ]
            , th [] [ text <| .taskKind <| taskEditorLanguage ]
            , th [] [ text <| .progress <| taskEditorLanguage ]
            , th [] [ text <| .simulation <| statisticsLanguage ]
            , th [] [ text <| .unit <| taskEditorLanguage ]
            , th [] [ text <| .counting <| taskEditorLanguage ]
            , th [] [ text <| .mean <| statisticsLanguage ]
            ]
                ++ deltaColumns
        , style = Style.classes.taskEditTable
        }


taskInfoColumns :
    Int
    -> Page.ViewType
    -> Page.TaskAnalysis
    -> List (HtmlUtil.Column msg)
taskInfoColumns index viewType taskAnalysis =
    let
        mean =
            taskAnalysis.incompleteTaskStatistics |> Maybe.Extra.unwrap "" (.mean >> bigDecimalToString)

        differenceAfterOneMoreTotal =
            taskAnalysis.incompleteTaskStatistics
                |> Maybe.Extra.unwrap "" (.total >> .one >> bigDecimalToString)

        differenceAfterOneMoreCounting =
            taskAnalysis.incompleteTaskStatistics
                |> Maybe.Extra.unwrap "" (.counting >> .one >> bigDecimalToString)

        afterCompletionTotal =
            taskAnalysis.incompleteTaskStatistics
                |> Maybe.Extra.unwrap "" (.total >> .completion >> bigDecimalToString)

        afterCompletionCounting =
            taskAnalysis.incompleteTaskStatistics
                |> Maybe.Extra.unwrap "" (.counting >> .completion >> bigDecimalToString)

        extraColumns =
            case viewType of
                Page.Total ->
                    [ { attributes = [ Style.classes.editable ]
                      , children = [ text <| differenceAfterOneMoreTotal ]
                      }
                    , { attributes = [ Style.classes.editable ]
                      , children = [ text <| afterCompletionTotal ]
                      }
                    ]

                Page.Counting ->
                    [ { attributes = [ Style.classes.editable ]
                      , children = [ text <| differenceAfterOneMoreCounting ]
                      }
                    , { attributes = [ Style.classes.editable ]
                      , children = [ text <| afterCompletionCounting ]
                      }
                    ]
    in
    [ { attributes = [ Style.classes.editable, Style.classes.numberCell ]
      , children = [ text <| String.fromInt index ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| .name <| .task <| taskAnalysis ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| TaskKind.toString <| .taskKind <| .task <| taskAnalysis ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ Pages.Tasks.Tasks.View.displayProgress taskAnalysis.task.progress taskAnalysis.task.taskKind ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.Extra.unwrap "" (.reachedModifier >> BigInt.toString) <| .simulation <| taskAnalysis ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.withDefault "" <| .unit <| .task <| taskAnalysis ]
      }
    , { attributes = []
      , children = [ input [ type_ "checkbox", checked <| .counting <| .task <| taskAnalysis, disabled True ] [] ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| mean ]
      }
    ]
        ++ extraColumns


viewTask : Int -> Page.ViewType -> Page.ProjectId -> Page.TaskEditorLanguage -> Page.TaskAnalysis -> Bool -> List (Html Page.LogicMsg)
viewTask index viewType projectId language resolvedTask showControls =
    Pages.Util.ParentEditor.View.lineWith
        { rowWithControls =
            \t ->
                { display = taskInfoColumns index viewType t
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


updateTask : Int -> Page.TaskEditorLanguage -> Page.ProjectId -> Page.Task -> Page.TaskUpdate -> List (Html Page.LogicMsg)
updateTask index language projectId task update =
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
            List.repeat 3 <| td [] []

        --todo: Consider a more stable way of computing the number of values
        infoColumns =
            [ td [ Style.classes.editable ]
                [ text <| String.fromInt <| index ]
            , td [ Style.classes.editable ]
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
