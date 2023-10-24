module Pages.Tasks.Handler exposing (init, update)

import Monocle.Compose as Compose
import Pages.Tasks.Page as Page
import Pages.Tasks.Project.Handler
import Pages.Tasks.Tasks.Handler
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.Parent.Page
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Pages.View.TristateUtil as TristateUtil
import Result.Extra
import Types.Project.Id exposing (Id)
import Types.Project.Resolved
import Util.DictList as DictList
import Util.Editing as Editing


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial flags.authorizedAccess
    , initialFetch flags.authorizedAccess flags.projectId |> Cmd.map Tristate.Logic
    )


initialFetch : AuthorizedAccess -> Id -> Cmd Page.LogicMsg
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
            let
                {- todo: There is an issue here. The action that is performed is setting the tasks, and the project
                   at the same time (atomically). It would seem that one could simply reuse the two update functions
                   for the corresponding subcomponents, but this is not the case. Why? Because the tasks display a
                   parent-like behaviour, and are implemented via the parent editor. This means that they transform
                   the model from Initial to Main too early for this particular scenario, which in turn results in non-matching
                   model states for the `updateFromSubModel` function.

                   We keep this now as a compromise, to avoid boilerplate duplication.
                   However, the overall design could be improved.
                -}
                newModel =
                    result
                        |> Result.Extra.unpack (Tristate.toError model)
                            (\resolved ->
                                -- We assume that all subcommands are Cmd.none, otherwise collect the commands, and batch the result.
                                model
                                    |> updateLogicProject
                                        (resolved |> .project |> Ok |> Pages.Util.Parent.Page.GotFetchResponse)
                                    |> Tuple.first
                                    |> Tristate.mapInitial
                                        ((Page.lenses.initial.tasks
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
            in
            ( newModel
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
