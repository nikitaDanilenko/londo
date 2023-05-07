module Pages.Tasks.Handler exposing (..)

import Monocle.Compose as Compose
import Pages.Tasks.Page as Page
import Pages.Tasks.Project.Handler
import Pages.Tasks.Tasks.Handler
import Pages.Tasks.Tasks.Page
import Pages.Util.Parent.Page
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Pages.View.TristateUtil as TristateUtil
import Result.Extra
import Util.DictList as DictList
import Util.Editing as Editing


update =
    Tristate.updateWith updateLogic


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        updateLogicProject =
            TristateUtil.updateFromSubModel
                { initialSubModelLens = Page.lenses.initial.project
                , mainSubModelLens = Page.lenses.main.project
                , subModelOf = Page.projectSubModel
                , fromInitToMain = Page.initialToMain
                , updateSubModel = Pages.Tasks.Project.Handler.updateLogic
                , toMsg = Page.ProjectMsg
                }

        updateLogicTasks =
            TristateUtil.updateFromSubModel
                { initialSubModelLens = Page.lenses.initial.tasks
                , mainSubModelLens = Page.lenses.main.tasks
                , subModelOf = Page.tasksSubModel
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
