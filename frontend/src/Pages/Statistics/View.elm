module Pages.Statistics.View exposing (view)

import BigInt exposing (BigInt)
import BigRational exposing (BigRational)
import Configuration exposing (Configuration)
import Html exposing (Html, button, div, h1, h2, hr, input, section, table, tbody, td, text, th, thead, tr)
import Html.Attributes exposing (checked, colspan, disabled, type_)
import Html.Events exposing (onClick)
import Html.Events.Extra exposing (onEnter)
import List.Extra
import LondoGQL.Enum.TaskKind as TaskKind
import Math.Natural as Natural
import Math.Positive as Positive
import Math.Statistics
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
import Types.Progress.Progress
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
        viewDashboard main.languages.statistics
            main.languages.dashboard
            main.dashboard
            groupedTasks
            :: (main.projects
                    |> DictList.values
                    |> List.map
                        (viewResolvedProject main.languages.taskEditor main.languages.statistics)
               )


numberOfDecimalPlaces : Int
numberOfDecimalPlaces =
    6


rationalToString : BigRational -> String
rationalToString =
    BigRational.toDecimalString numberOfDecimalPlaces


toPercentageString : { numerator : BigInt, denominator : BigInt } -> String
toPercentageString =
    Types.Progress.Progress.fromBigIntsOrDefaults
        >> Types.Progress.Progress.toPercentRational
        >> rationalToString


viewDashboard : Page.StatisticsLanguage -> Page.DashboardLanguage -> Page.Dashboard -> List (List Page.ResolvedTask) -> Html Page.LogicMsg
viewDashboard statisticsLanguage dashboardLanguage dashboard resolvedTasks =
    let
        tasks =
            resolvedTasks |> List.map (List.map .task)

        reachableAll =
            tasks |> Math.Statistics.sumWith (reachableInProject { countedOnly = False })

        reachableAllCounted =
            tasks |> Math.Statistics.sumWith (reachableInProject { countedOnly = True })

        reachedAll =
            tasks |> Math.Statistics.sumWith (reachedInProject { countedOnly = False })

        reachedAllCounted =
            tasks |> Math.Statistics.sumWith (reachedInProject { countedOnly = True })

        -- todo: Consider extraction for better testability
        reachedSimulatedAll =
            resolvedTasks
                |> List.concatMap (List.map (.simulation >> Maybe.map .reachedModifier))
                |> Maybe.Extra.values
                |> List.sum
                |> BigInt.fromInt
                |> BigInt.add reachedAll

        -- todo: Consider extraction for better testability
        reachedSimulatedAllCounted =
            resolvedTasks
                |> List.concat
                |> List.filterMap (\resolved -> resolved.simulation |> Maybe.map .reachedModifier |> Maybe.Extra.filter (\_ -> resolved.task.counting))
                |> List.sum
                |> BigInt.fromInt
                |> BigInt.add reachedAllCounted

        meanAbsoluteTotal =
            { numerator = reachedAll
            , denominator = reachableAll
            }
                |> toPercentageString

        meanAbsoluteCounted =
            { numerator = reachedAllCounted
            , denominator = reachableAllCounted
            }
                |> toPercentageString

        meanAbsoluteSimulatedTotal =
            { numerator = reachedSimulatedAll
            , denominator = reachableAll
            }
                |> toPercentageString

        meanAbsoluteSimulatedCounted =
            { numerator = reachedSimulatedAllCounted
            , denominator = reachableAllCounted
            }
                |> toPercentageString

        allProgresses =
            tasks |> List.concat |> List.map .progress

        numberOfAllTasks =
            allProgresses
                |> List.length

        countingProgresses =
            tasks
                |> List.concat
                |> List.filterMap (Just >> Maybe.Extra.filter .counting >> Maybe.map .progress)

        meanRelative =
            Positive.fromInt numberOfAllTasks
                |> Maybe.Extra.unwrap (BigRational.fromInt 0) (\p -> Math.Statistics.relative p allProgresses)

        numberOfCountingTasks =
            countingProgresses |> List.length

        meanRelativeCounted =
            Positive.fromInt numberOfCountingTasks
                |> Maybe.Extra.unwrap (BigRational.fromInt 0) (\p -> Math.Statistics.relative p countingProgresses)
    in
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
                        [ text <| BigInt.toString <| reachedAll
                        ]
                    , td []
                        [ text <| BigInt.toString <| reachedAllCounted
                        ]
                    , td [] [ text <| BigInt.toString <| reachedSimulatedAll ]
                    , td [] [ text <| BigInt.toString <| reachedSimulatedAllCounted ]
                    ]
                , tr [ Style.classes.editing, Style.classes.statisticsLine ]
                    [ td [] [ text <| .reachableAll <| statisticsLanguage ]
                    , td []
                        [ text <|
                            BigInt.toString <|
                                reachableAll
                        ]
                    , td []
                        [ text <|
                            BigInt.toString <|
                                List.foldl BigInt.add (BigInt.fromInt 0) <|
                                    List.map (reachableInProject { countedOnly = True }) <|
                                        tasks
                        ]
                    , td [] []
                    , td [] []
                    ]
                , tr [ Style.classes.editing, Style.classes.statisticsLine ]
                    [ td [] [ text <| .meanAbsolute <| statisticsLanguage ]
                    , td [] [ text <| meanAbsoluteTotal ]
                    , td [] [ text <| meanAbsoluteCounted ]
                    , td [] [ text <| meanAbsoluteSimulatedTotal ]
                    , td [] [ text <| meanAbsoluteSimulatedCounted ]
                    ]
                , tr [ Style.classes.editing, Style.classes.statisticsLine ]
                    [ td [] [ text <| .meanRelative <| statisticsLanguage ]
                    , td [] [ text <| BigRational.toDecimalString numberOfDecimalPlaces <| meanRelative ]
                    , td [] [ text <| BigRational.toDecimalString numberOfDecimalPlaces <| meanRelativeCounted ]
                    ]
                ]
            ]
        ]


reachableInProject : { countedOnly : Bool } -> List Page.Task -> BigInt
reachableInProject ps =
    let
        predicate =
            if ps.countedOnly then
                .counting

            else
                always True
    in
    List.filterMap
        (Just
            >> Maybe.Extra.filter predicate
            >> Maybe.map (.progress >> .reachable)
        )
        >> Positive.sum


reachedInProject : { countedOnly : Bool } -> List Page.Task -> BigInt
reachedInProject ps =
    let
        predicate =
            if ps.countedOnly then
                .counting

            else
                always True
    in
    List.filterMap
        (Just
            >> Maybe.Extra.filter predicate
            >> Maybe.map (.progress >> .reached)
        )
        >> Natural.sum


viewResolvedProject : Page.TaskEditorLanguage -> Page.StatisticsLanguage -> EditingResolvedProject -> Html Page.LogicMsg
viewResolvedProject taskEditorLanguage statisticsLanguage resolvedProject =
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

        display =
            List.sortBy (.original >> .task >> .name)
                >> List.concatMap
                    (Editing.unpack
                        { onView = viewTask project.id taskEditorLanguage (tasks |> List.map .original)
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
                            [ unfinished |> display
                            , separator
                            , finished |> display
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



--todo: Only the number of tasks is relevant (at least it seems so)


taskInfoColumns : List Page.ResolvedTask -> Page.ResolvedTask -> List (HtmlUtil.Column msg)
taskInfoColumns allTasks resolvedTask =
    let
        numberOfAllTasks =
            allTasks |> List.length

        numberOfCountedTasks =
            allTasks |> List.Extra.count (.task >> .counting)

        progress =
            resolvedTask.task.progress

        mean =
            progress
                |> Types.Progress.Progress.toPercentRational
                |> rationalToString

        differenceAfterOneMoreExactTotal =
            Math.Statistics.differenceAfterOneMoreExact
                { numberOfElements = numberOfAllTasks }
                progress

        differenceAfterOneMoreExactCounted =
            Math.Statistics.differenceAfterOneMoreExact
                { numberOfElements = numberOfCountedTasks }
                progress

        afterCompletionExactTotal =
            Math.Statistics.differenceAfterCompletionExact
                { numberOfElements = numberOfAllTasks }
                progress

        afterCompletionExactCounted =
            Math.Statistics.differenceAfterCompletionExact
                { numberOfElements = numberOfCountedTasks }
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
      , children = [ text <| Maybe.Extra.unwrap "" (.reachedModifier >> String.fromInt) <| .simulation <| resolvedTask ]
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


viewTask : Page.ProjectId -> Page.TaskEditorLanguage -> List Page.ResolvedTask -> Page.ResolvedTask -> Bool -> List (Html Page.LogicMsg)
viewTask projectId language allTasks resolvedTask showControls =
    Pages.Util.ParentEditor.View.lineWith
        { rowWithControls =
            \t ->
                { display = taskInfoColumns allTasks t
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
