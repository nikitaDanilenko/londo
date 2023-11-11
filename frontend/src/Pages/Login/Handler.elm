module Pages.Login.Handler exposing (init, update)

import Addresses.Frontend
import Browser.Navigation
import Pages.Login.Page as Page
import Pages.Util.Links as Links
import Pages.View.Tristate as Tristate
import Ports
import Result.Extra
import Types.User.Login


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
        setCredentials credentials =
            ( model
                |> Tristate.mapMain
                    (Page.lenses.main.credentials.set credentials)
            , Cmd.none
            )

        login =
            ( model
            , model
                |> Tristate.foldMain Cmd.none
                    (\main ->
                        Types.User.Login.loginWith
                            Page.GotResponse
                            model.configuration
                            main.credentials
                    )
            )

        gotResponse remoteData =
            remoteData
                |> Result.Extra.unpack
                    (\error ->
                        ( Tristate.toError model error
                        , Cmd.none
                        )
                    )
                    (\token ->
                        ( model
                        , Cmd.batch
                            [ Ports.storeToken token
                            , Addresses.Frontend.overview.address () |> Links.frontendPage model.configuration |> Browser.Navigation.load
                            ]
                        )
                    )
    in
    case msg of
        Page.SetCredentials credentials ->
            setCredentials credentials

        Page.Login ->
            login

        Page.GotResponse remoteData ->
            gotResponse remoteData
