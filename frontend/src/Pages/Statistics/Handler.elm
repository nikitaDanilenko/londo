module Pages.Statistics.Handler exposing (init, update, updateLogic)

import Language.Language
import Math.Positive
import Maybe.Extra
import Monocle.Compose as Compose
import Monocle.Lens as Lens exposing (Lens)
import Pages.Statistics.EditingResolvedProject as EditingResolvedProject
import Pages.Statistics.Page as Page
import Pages.Statistics.Pagination as Pagination
import Pages.Util.PaginationSettings as PaginationSettings
import Pages.View.Tristate as Tristate
import Result.Extra
import Types.Dashboard.Analysis
import Types.Task.Analysis
import Types.Task.TaskWithSimulation
import Util.DictList as DictList exposing (DictList)
import Util.Editing as Editing exposing (Editing)
import Util.LensUtil as LensUtil


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial
        { taskEditor = Language.Language.default.taskEditor
        , project = Language.Language.default.projectEditor
        , dashboard = Language.Language.default.dashboardEditor
        , statistics = Language.Language.default.statistics
        }
        flags.authorizedAccess
    , Types.Dashboard.Analysis.fetchWith
        Page.GotFetchDashboardAnalysisResponse
        flags.authorizedAccess
        flags.dashboardId
        Page.numberOfDecimalPlaces
        |> Cmd.map Tristate.Logic
    )


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        gotFetchDashboardAnalysisResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\serverResponse ->
                        model
                            |> Tristate.mapInitial
                                (Page.lenses.initial.dashboardAnalysis.set
                                    (serverResponse |> Just)
                                )
                            |> Tristate.fromInitToMain Page.initialToMain
                    )
            , Cmd.none
            )

        editTask projectId taskId taskUpdate =
            ( model |> updateTaskById projectId taskId (Editing.lenses.update.set taskUpdate)
            , Cmd.none
            )

        saveEditTask projectId taskId =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        let
                            numberOf filter =
                                main
                                    |> Page.lenses.main.projects.get
                                    |> DictList.values
                                    |> List.concatMap (.tasks >> DictList.values >> filter)
                                    |> List.length
                                    |> Math.Positive.fromInt

                            numberOfTotalTasks =
                                numberOf identity

                            numberOfcountingTasks =
                                numberOf (List.filter (.original >> .task >> .counting))
                        in
                        main
                            |> Page.lenses.main.projects.get
                            |> DictList.get projectId
                            |> Maybe.andThen (.tasks >> DictList.get taskId)
                            |> Maybe.andThen Editing.extractUpdate
                            |> Maybe.Extra.unwrap
                                Cmd.none
                                (Types.Task.TaskWithSimulation.updateWith
                                    (Page.GotSaveEditTaskResponse projectId)
                                    { configuration = model.configuration
                                    , jwt = main.jwt
                                    }
                                    { dashboardId = main.dashboard.id
                                    , taskId = taskId
                                    , numberOfTotalTasks = numberOfTotalTasks
                                    , numberOfcountingTasks = numberOfcountingTasks
                                    , numberOfDecimalPlaces = Page.numberOfDecimalPlaces
                                    }
                                )
                    )
            )

        gotSaveEditTaskResponse projectId result =
            result
                |> Result.Extra.unpack (\error -> ( Tristate.toError model error, Cmd.none ))
                    (\resolvedTask ->
                        ( model
                        , model
                            |> Tristate.foldMain Cmd.none
                                (\main ->
                                    Types.Dashboard.Analysis.fetchWithSelection
                                        { expect = Page.GotFetchUpdatedStatisticsResponse projectId resolvedTask
                                        , selection = Types.Dashboard.Analysis.statisticsSelection
                                        }
                                        { configuration = model.configuration
                                        , jwt = main.jwt
                                        }
                                        main.dashboard.id
                                        Page.numberOfDecimalPlaces
                                )
                        )
                    )

        gotFetchUpdatedStatisticsResponse projectId resolvedTask result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\dashboardStatistics ->
                        model
                            |> updateTaskById projectId
                                resolvedTask.task.id
                                (Editing.asViewWithElement
                                    { task = resolvedTask.task
                                    , simulation = resolvedTask.simulation
                                    , incompleteTaskStatistics = resolvedTask.incompleteTaskStatistics
                                    }
                                    >> Editing.toggleControls
                                )
                            |> Tristate.mapMain (Page.lenses.main.dashboardStatistics.set dashboardStatistics)
                    )
            , Cmd.none
            )

        toggleControls projectId taskId =
            ( model
                |> updateTaskById projectId taskId Editing.toggleControls
            , Cmd.none
            )

        enterEditTask projectId taskId =
            ( model
                |> updateTaskById projectId taskId (Editing.toUpdate Types.Task.TaskWithSimulation.from)
            , Cmd.none
            )

        exitEditTask projectId taskId =
            ( model |> updateTaskById projectId taskId Editing.toView
            , Cmd.none
            )

        setSearchString string =
            ( model
                |> Tristate.mapMain
                    (PaginationSettings.setSearchStringAndReset
                        { searchStringLens =
                            Page.lenses.main.searchString
                        , paginationSettingsLens =
                            Page.lenses.main.pagination
                                |> Compose.lensWithLens Pagination.lenses.projects
                        }
                        string
                    )
            , Cmd.none
            )

        setProjectsPagination paginationSettings =
            ( model
                |> Tristate.mapMain ((Page.lenses.main.pagination |> Compose.lensWithLens Pagination.lenses.projects).set paginationSettings)
            , Cmd.none
            )

        setProjectPagination projectId taskStatus paginationSettings =
            ( model
                |> Tristate.mapMain
                    (Lens.modify Page.lenses.main.pagination
                        (case taskStatus of
                            Page.Unfinished ->
                                (Pagination.lenses.unfinishedTasks
                                    |> Compose.lensWithOptional (LensUtil.dictByKey projectId)
                                ).set
                                    paginationSettings

                            Page.Finished ->
                                (Pagination.lenses.finishedTasks
                                    |> Compose.lensWithOptional (LensUtil.dictByKey projectId)
                                ).set
                                    paginationSettings
                        )
                    )
            , Cmd.none
            )
    in
    case msg of
        Page.GotFetchDashboardAnalysisResponse result ->
            gotFetchDashboardAnalysisResponse result

        Page.EditTask projectId taskId taskUpdate ->
            editTask projectId taskId taskUpdate

        Page.SaveEditTask projectId taskId ->
            saveEditTask projectId taskId

        Page.GotSaveEditTaskResponse projectId result ->
            gotSaveEditTaskResponse projectId result

        Page.GotFetchUpdatedStatisticsResponse projectId resolvedTask result ->
            gotFetchUpdatedStatisticsResponse projectId resolvedTask result

        Page.ToggleControls projectId taskId ->
            toggleControls projectId taskId

        Page.EnterEditTask projectId taskId ->
            enterEditTask projectId taskId

        Page.ExitEditTask projectId taskId ->
            exitEditTask projectId taskId

        Page.SetProjectsPagination pagination ->
            setProjectsPagination pagination

        Page.SetProjectPagination projectId taskStatus pagination ->
            setProjectPagination projectId taskStatus pagination

        Page.SetSearchString string ->
            setSearchString string


updateTaskById :
    Page.ProjectId
    -> Page.TaskId
    -> (Editing Page.TaskAnalysis Page.TaskUpdate -> Editing Page.TaskAnalysis Page.TaskUpdate)
    -> Page.Model
    -> Page.Model
updateTaskById projectId taskId =
    LensUtil.updateById taskId EditingResolvedProject.lenses.tasks
        >> LensUtil.updateById projectId Page.lenses.main.projects
        >> Tristate.mapMain
