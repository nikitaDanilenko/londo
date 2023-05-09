module Pages.Tasks.Handler exposing (init, update)

import Monocle.Compose as Compose
import Pages.Tasks.Page as Page
import Pages.Tasks.Project.Handler
import Pages.Tasks.Tasks.Handler
import Pages.Tasks.Tasks.Page
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.Parent.Page
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Pages.View.TristateUtil as TristateUtil
import Result.Extra
import Types.Project.ProjectId exposing (ProjectId)
import Types.Project.Resolved
import Util.DictList as DictList
import Util.Editing as Editing


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
                        model
                            |> Tristate.mapInitial
                                ((Page.lenses.initial.project
                                    |> Compose.lensWithLens Pages.Util.Parent.Page.lenses.initial.parent
                                 ).set
                                    (resolved.project |> Just)
                                    >> (Page.lenses.initial.tasks
                                            |> Compose.lensWithLens Pages.Tasks.Tasks.Page.lenses.initial
                                            |> Compose.lensWithLens Pages.Util.ParentEditor.Page.lenses.initial.parents
                                       ).set
                                        (resolved.tasks
                                            |> List.map Editing.asView
                                            |> DictList.fromListWithKey (.original >> .id)
                                            |> Just
                                        )
                                )
                            |> Tristate.fromInitToMain Page.initialToMain
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
