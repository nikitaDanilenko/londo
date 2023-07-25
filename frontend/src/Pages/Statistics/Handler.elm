module Pages.Statistics.Handler exposing (updateLogic)

import Maybe.Extra
import Monocle.Compose as Compose
import Monocle.Lens exposing (Lens)
import Pages.Statistics.EditingResolvedProject as EditingResolvedProject
import Pages.Statistics.Page as Page
import Pages.Statistics.Pagination as Pagination
import Pages.Util.PaginationSettings as PaginationSettings
import Pages.View.Tristate as Tristate
import Result.Extra
import Types.Task.Update
import Util.DictList as DictList exposing (DictList)
import Util.Editing as Editing exposing (Editing)
import Util.HttpUtil as HttpUtil
import Util.LensUtil as LensUtil


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        gotResponse :
            { lens : Lens Page.Initial (Maybe response)
            }
            -> HttpUtil.GraphQLResult response
            -> Page.Model
        gotResponse ps =
            Result.Extra.unpack (Tristate.toError model)
                (\serverResponse ->
                    model
                        |> Tristate.mapInitial
                            (ps.lens.set
                                (serverResponse |> Just)
                            )
                        |> Tristate.fromInitToMain Page.initialToMain
                )

        gotFetchDashboardResponse result =
            ( gotResponse
                { lens = Page.lenses.initial.dashboard
                }
                result
            , Cmd.none
            )

        gotFetchProjectsResponse result =
            ( gotResponse
                { lens = Page.lenses.initial.projects
                }
                result
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
                                (Types.Task.Update.updateWith
                                    (\result -> ( projectId, result ) |> Page.GotSaveEditTaskResponse)
                                    { configuration = model.configuration
                                    , jwt = main.jwt
                                    }
                                    taskId
                                )
                    )
            )

        gotSaveEditTaskResponse ( projectId, result ) =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\task ->
                        model |> updateTaskById projectId task.id (Editing.asViewWithElement task)
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
                |> updateTaskById projectId taskId (Editing.toUpdate Types.Task.Update.from)
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

        setPagination pagination =
            ( model
                |> Tristate.mapMain (Page.lenses.main.pagination.set pagination)
            , Cmd.none
            )
    in
    case msg of
        Page.GotFetchDashboardResponse result ->
            gotFetchDashboardResponse result

        Page.GotFetchProjectsResponse result ->
            gotFetchProjectsResponse result

        Page.EditTask projectId taskId taskUpdate ->
            editTask projectId taskId taskUpdate

        Page.SaveEditTask projectId taskId ->
            saveEditTask projectId taskId

        Page.GotSaveEditTaskResponse result ->
            gotSaveEditTaskResponse result

        Page.ToggleControls projectId taskId ->
            toggleControls projectId taskId

        Page.EnterEditTask projectId taskId ->
            enterEditTask projectId taskId

        Page.ExitEditTask projectId taskId ->
            exitEditTask projectId taskId

        Page.SetPagination pagination ->
            setPagination pagination

        Page.SetSearchString string ->
            setSearchString string


updateTaskById :
    Page.ProjectId
    -> Page.TaskId
    -> (Editing Page.Task Page.TaskUpdate -> Editing Page.Task Page.TaskUpdate)
    -> Page.Model
    -> Page.Model
updateTaskById projectId taskId =
    LensUtil.updateById taskId EditingResolvedProject.lenses.tasks
        >> LensUtil.updateById projectId Page.lenses.main.projects
        >> Tristate.mapMain
