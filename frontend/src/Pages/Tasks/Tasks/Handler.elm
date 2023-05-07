module Pages.Tasks.Tasks.Handler exposing (updateLogic)

import Pages.Tasks.Tasks.Page as Page
import Pages.Util.ParentEditor.Handler
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.Task.Creation
import Types.Task.Task
import Types.Task.Update


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        ( newTristate, cmd ) =
            Pages.Util.ParentEditor.Handler.updateLogic
                { idOfParent = .id
                , toUpdate = Types.Task.Update.from
                , navigateToAddress = \_ -> Nothing
                , create = Types.Task.Creation.createWith Pages.Util.ParentEditor.Page.GotCreateResponse
                , save = Types.Task.Update.updateWith Pages.Util.ParentEditor.Page.GotSaveEditResponse
                , delete = Types.Task.Task.deleteWith Pages.Util.ParentEditor.Page.GotDeleteResponse
                }
                msg
                (model
                    |> Tristate.fold
                        { onInitial = .initial
                        , onMain = .main
                        , onError = identity
                        }
                )
    in
    ( newTristate, cmd )