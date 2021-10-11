module Pages.Login.Login exposing (..)

import Configuration exposing (Configuration)
import Graphql.Http
import Graphql.Operation exposing (RootMutation)
import Graphql.SelectionSet exposing (SelectionSet)
import Html exposing (Html, button, div, input, label, text)
import Html.Attributes exposing (autocomplete, class, for, id, type_)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Language.Language as Language exposing (Language)
import LondoGQL.Mutation as Mutation
import Pages.Util.TriState as TriState exposing (TriState(..))
import RemoteData exposing (RemoteData)


type alias Model =
    { user : String
    , password : String
    , loginLanguage : Language.Login
    , isValidityUnrestricted : Bool
    , state : TriState
    , configuration : Configuration
    }


updateUser : Model -> String -> Model
updateUser model user =
    { model | user = user }


updatePassword : Model -> String -> Model
updatePassword model password =
    { model | password = password }


updateState : Model -> TriState -> Model
updateState model state =
    { model | state = state }


updateIsValidityUnrestricted : Model -> Bool -> Model
updateIsValidityUnrestricted model isValidityUnrestricted =
    { model | isValidityUnrestricted = isValidityUnrestricted }


type Msg
    = SetUser String
    | SetPassword String
    | SetIsValidityUnrestricted Bool
    | Login
    | GotResponse (RemoteData (Graphql.Http.Error String) String)


type alias Flags =
    { loginLanguage : Language.Login
    , configuration : Configuration
    }


init : Flags -> Model
init flags =
    { user = ""
    , password = ""
    , isValidityUnrestricted = True
    , loginLanguage = flags.loginLanguage
    , state = Initial
    , configuration = flags.configuration
    }


view : Model -> Html Msg
view model =
    div [ id "initialMain" ]
            [ div [ id "userField" ]
                [ label [ for "user" ] [ text model.loginLanguage.nickname ]
                , input [ autocomplete True, onInput SetUser, onEnter Login ] []
                ]
            , div [ id "passwordField" ]
                [ label [ for "password" ] [ text model.loginLanguage.password ]
                , input [ type_ "password", autocomplete True, onInput SetPassword, onEnter Login ] []
                ]
            , div [ id "fetchButton" ]
                [ button [ class "button", onClick Login ] [ text model.loginLanguage.login ] ]
            ]


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        SetUser user ->
            ( updateUser model user, Cmd.none )

        SetPassword password ->
            ( updatePassword model password, Cmd.none )

        SetIsValidityUnrestricted isValidityUnrestricted ->
            ( updateIsValidityUnrestricted model isValidityUnrestricted, Cmd.none )

        Login ->
            ( model, makeRequest model )

        GotResponse remoteData ->
            ( updateState model (TriState.fromRemoteData remoteData), Cmd.none )


loginQuery : Model -> SelectionSet String RootMutation
loginQuery model =
    Mutation.login { nickname = model.user, password = model.password, isValidityUnrestricted = model.isValidityUnrestricted }


makeRequest : Model -> Cmd Msg
makeRequest model =
    loginQuery model
        |> Graphql.Http.mutationRequest model.configuration.graphQLEndpoint
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)
