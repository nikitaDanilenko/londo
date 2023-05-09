module Pages.Tasks.Handler exposing (init, update)

import Pages.Tasks.Page as Page
import Pages.Tasks.Project.Handler
import Pages.Tasks.Tasks.Handler
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.Parent.Page
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Pages.View.TristateUtil as TristateUtil
import Result.Extra
import Types.Project.ProjectId exposing (ProjectId)
import Types.Project.Resolved


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial flags.authorizedAccess flags.projectId
    , initialFetch flags.authorizedAccess flags.projectId |> Cmd.map Tristate.Logic
    )


initialFetch : AuthorizedAccess -> ProjectId -> Cmd Page.LogicMsg
initialFetch =
    Types.Project.Resolved.fetchWith Page.GotFetchResponse


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        updateLogicProject =
            TristateUtil.updateFromSubModel
                { initialSubModelLens = Page.lenses.initial.project
                , mainSubModelLens = Page.lenses.main.project
                , fromInitToMain = Page.initialToMain
                , updateSubModel = Pages.Tasks.Project.Handler.updateLogic
                , toMsg = Page.ProjectMsg
                }

        updateLogicTasks =
            TristateUtil.updateFromSubModel
                { initialSubModelLens = Page.lenses.initial.tasks
                , mainSubModelLens = Page.lenses.main.tasks
                , fromInitToMain = Page.initialToMain
                , updateSubModel = Pages.Tasks.Tasks.Handler.updateLogic
                , toMsg = Page.TasksMsg
                }

        gotFetchResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\resolved ->
                        -- We assume that all subcommands are Cmd.none, otherwise collect the commands, and batch the result.
                        model
                            |> TristateUtil.updateFromSubModel
                                { initialSubModelLens = Page.lenses.initial.project
                                , mainSubModelLens = Page.lenses.main.project
                                , fromInitToMain = Page.initialToMain
                                , updateSubModel = Pages.Tasks.Project.Handler.updateLogic
                                , toMsg = Page.ProjectMsg
                                }
                                (resolved |> .project |> Ok |> Pages.Util.Parent.Page.GotFetchResponse)
                            |> Tuple.first
                            |> TristateUtil.updateFromSubModel
                                { initialSubModelLens = Page.lenses.initial.tasks
                                , mainSubModelLens = Page.lenses.main.tasks
                                , fromInitToMain = Page.initialToMain
                                , updateSubModel = Pages.Tasks.Tasks.Handler.updateLogic
                                , toMsg = Page.TasksMsg
                                }
                                (resolved |> .tasks |> Ok |> Pages.Util.ParentEditor.Page.GotFetchResponse)
                            |> Tuple.first
                    )
            , Cmd.none
            )
    in
    case msg of
        Page.GotFetchResponse result ->
            gotFetchResponse result

        Page.ProjectMsg projectMsg ->
            updateLogicProject
                projectMsg
                model

        Page.TasksMsg tasksMsg ->
            updateLogicTasks
                tasksMsg
                model
