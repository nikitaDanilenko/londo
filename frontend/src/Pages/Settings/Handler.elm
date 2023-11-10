module Pages.Settings.Handler exposing (..)

import Addresses.Frontend
import Browser.Navigation
import Monocle.Compose as Compose
import Pages.Settings.Page as Page
import Pages.Util.ComplementInput as ComplementInput
import Pages.Util.Links as Links
import Pages.Util.PasswordInput as PasswordInput
import Pages.View.Tristate as Tristate
import Ports
import Result.Extra
import Types.User.PasswordUpdate
import Types.User.Update
import Types.User.User


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial flags.authorizedAccess
    , Types.User.User.fetchWith Page.GotFetchUserResponse flags.authorizedAccess
        |> Cmd.map Tristate.Logic
    )


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        passwordInputLens =
            Page.lenses.main.complementInput |> Compose.lensWithLens ComplementInput.lenses.passwordInput

        displayNameLens =
            Page.lenses.main.complementInput |> Compose.lensWithLens ComplementInput.lenses.displayName

        gotFetchUserResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\user ->
                        model
                            |> Tristate.mapInitial
                                (Page.lenses.initial.user.set (user |> Just))
                            |> Tristate.fromInitToMain Page.initialToMain
                    )
            , Cmd.none
            )

        updatePassword =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        main
                            |> passwordInputLens.get
                            |> Types.User.PasswordUpdate.ClientInput
                            |> Types.User.PasswordUpdate.updateWith Page.GotUpdatePasswordResponse
                                { jwt = main.jwt
                                , configuration = model.configuration
                                }
                    )
            )

        gotUpdatePasswordResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\isSuccess ->
                        if isSuccess then
                            model
                                |> Tristate.mapMain (passwordInputLens.set PasswordInput.initial)

                        else
                            model
                    )
            , Cmd.none
            )

        updateSettings =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        main
                            |> displayNameLens.get
                            |> Types.User.Update.ClientInput
                            |> Types.User.Update.updateWith Page.GotUpdateSettingsResponse
                                { jwt = main.jwt
                                , configuration = model.configuration
                                }
                    )
            )

        gotUpdateSettingsResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\user ->
                        model
                            |> Tristate.mapMain
                                (Page.lenses.main.user.set user)
                     -- todo: Do we need to set the displayNameLens as well?
                    )
            , Cmd.none
            )

        requestDeletion =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        Types.User.User.requestDeletionWith
                            Page.GotRequestDeletionResponse
                            { jwt = main.jwt
                            , configuration = model.configuration
                            }
                    )
            )

        gotRequestDeletionResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\_ -> model |> Tristate.mapMain (Page.lenses.main.mode.set Page.RequestedDeletion))
            , Cmd.none
            )

        setComplementInput complementInput =
            ( model
                |> Tristate.mapMain (Page.lenses.main.complementInput.set complementInput)
            , Cmd.none
            )

        logout logoutMode =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        Types.User.User.logoutWith
                            Page.GotLogoutResponse
                            { jwt = main.jwt
                            , configuration = model.configuration
                            }
                            logoutMode
                    )
            )

        gotLogoutResponse result =
            result
                |> Result.Extra.unpack (\error -> ( error |> Tristate.toError model, Cmd.none ))
                    (\isSuccess ->
                        if isSuccess then
                            ( model
                            , Cmd.batch
                                [ Ports.doDeleteToken ()
                                , ()
                                    |> Addresses.Frontend.login.address
                                    |> Links.frontendPage model.configuration
                                    |> Browser.Navigation.load
                                ]
                            )

                        else
                            ( model, Cmd.none )
                    )
    in
    case msg of
        Page.GotFetchUserResponse result ->
            gotFetchUserResponse result

        Page.UpdatePassword ->
            updatePassword

        Page.GotUpdatePasswordResponse result ->
            gotUpdatePasswordResponse result

        Page.UpdateSettings ->
            updateSettings

        Page.GotUpdateSettingsResponse result ->
            gotUpdateSettingsResponse result

        Page.RequestDeletion ->
            requestDeletion

        Page.GotRequestDeletionResponse result ->
            gotRequestDeletionResponse result

        Page.SetComplementInput complementInput ->
            setComplementInput complementInput

        Page.Logout logoutMode ->
            logout logoutMode

        Page.GotLogoutResponse result ->
            gotLogoutResponse result
