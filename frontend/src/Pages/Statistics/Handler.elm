module Pages.Statistics.Handler exposing (init, update, updateLogic)

import Language.Language
import Maybe.Extra
import Monocle.Compose as Compose
import Monocle.Lens as Lens exposing (Lens)
import Pages.Statistics.EditingResolvedProject as EditingResolvedProject
import Pages.Statistics.Page as Page
import Pages.Statistics.Pagination as Pagination
import Pages.Util.PaginationSettings as PaginationSettings
import Pages.View.Tristate as Tristate
import Result.Extra
import Types.Dashboard.DeeplyResolved
import Types.Task.Resolved
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
    , Types.Dashboard.DeeplyResolved.fetchWith
        Page.GotFetchDeeplyDashboardResponse
        flags.authorizedAccess
        flags.dashboardId
        |> Cmd.map Tristate.Logic
    )


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        gotFetchDeeplyResolvedDashboardResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\serverResponse ->
                        model
                            |> Tristate.mapInitial
                                (Page.lenses.initial.deeplyResolvedDashboard.set
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
                                    main.dashboard.id
                                    taskId
                                )
                    )
            )

        gotSaveEditTaskResponse projectId result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\resolvedTask ->
                        model
                            |> updateTaskById projectId
                                resolvedTask.task.id
                                (Editing.asViewWithElement
                                    { task = resolvedTask.task
                                    , simulation = resolvedTask.simulation
                                    }
                                    >> Editing.toggleControls
                                )
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
        Page.GotFetchDeeplyDashboardResponse result ->
            gotFetchDeeplyResolvedDashboardResponse result

        Page.EditTask projectId taskId taskUpdate ->
            editTask projectId taskId taskUpdate

        Page.SaveEditTask projectId taskId ->
            saveEditTask projectId taskId

        Page.GotSaveEditTaskResponse projectId result ->
            gotSaveEditTaskResponse projectId result

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
    -> (Editing Page.ResolvedTask Page.TaskUpdate -> Editing Page.ResolvedTask Page.TaskUpdate)
    -> Page.Model
    -> Page.Model
updateTaskById projectId taskId =
    LensUtil.updateById taskId EditingResolvedProject.lenses.tasks
        >> LensUtil.updateById projectId Page.lenses.main.projects
        >> Tristate.mapMain
