module Pages.Login.Handler exposing (init, update)

import Addresses.Frontend
import Browser.Navigation
import Pages.Login.Page as Page
import Pages.Login.Requests as Requests
import Pages.Util.Links as Links
import Pages.View.Tristate as Tristate
import Ports
import Result.Extra
import Types.Credentials exposing (Credentials)
import Util.HttpUtil as HttpUtil


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
    case msg of
        Page.SetCredentials credentials ->
            setCredentials model credentials

        Page.Login ->
            login model

        Page.GotResponse remoteData ->
            gotResponse model remoteData


setCredentials : Page.Model -> Credentials -> ( Page.Model, Cmd Page.LogicMsg )
setCredentials model credentials =
    ( model
        |> Tristate.mapMain
            (Page.lenses.main.credentials.set credentials)
    , Cmd.none
    )


login : Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
login model =
    ( model
    , model
        |> Tristate.foldMain Cmd.none
            (\main ->
                Requests.login model.configuration main.credentials
            )
    )


gotResponse : Page.Model -> HttpUtil.GraphQLResult String -> ( Page.Model, Cmd Page.LogicMsg )
gotResponse model remoteData =
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
