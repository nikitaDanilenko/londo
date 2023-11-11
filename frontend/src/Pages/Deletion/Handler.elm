module Pages.Deletion.Handler exposing (init, update)

import Pages.Deletion.Page as Page
import Pages.View.Tristate as Tristate
import Ports
import Result.Extra
import Types.User.User


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial flags
    , Cmd.none
    )


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        confirm =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        main.deletionJWT
                            |> Types.User.User.confirmDeletionWith
                                Page.GotConfirmResponse
                                model.configuration
                    )
            )

        gotConfirmResponse result =
            result
                |> Result.Extra.unpack
                    (\error ->
                        ( error |> Tristate.toError model
                        , Cmd.none
                        )
                    )
                    (\_ ->
                        ( model |> Tristate.mapMain (Page.lenses.main.mode.set Page.Confirmed)
                        , Ports.doDeleteToken ()
                        )
                    )
    in
    case msg of
        Page.Confirm ->
            confirm

        Page.GotConfirmResponse result ->
            gotConfirmResponse result
