module Pages.Recovery.Confirm.Handler exposing (init, update)

import Pages.Recovery.Confirm.Page as Page
import Pages.Util.PasswordInput as PasswordInput exposing (PasswordInput)
import Pages.View.Tristate as Tristate
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
        setPasswordInput passwordInput =
            ( model |> Tristate.mapMain (Page.lenses.main.passwordInput.set passwordInput)
            , Cmd.none
            )

        confirm =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        Types.User.User.confirmRecoveryWith
                            Page.GotConfirmResponse
                            model.configuration
                            main.recoveryJwt
                            main.passwordInput.password1
                    )
            )

        gotConfirmResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\_ ->
                        model
                            |> Tristate.mapMain
                                (Page.lenses.main.mode.set Page.Confirmed
                                    >> Page.lenses.main.passwordInput.set PasswordInput.initial
                                )
                    )
            , Cmd.none
            )
    in
    case msg of
        Page.SetPasswordInput passwordInput ->
            setPasswordInput passwordInput

        Page.Confirm ->
            confirm

        Page.GotConfirmResponse result ->
            gotConfirmResponse result
