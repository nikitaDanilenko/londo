module Pages.Register.CreateRegistrationToken exposing (Flags, Model, Msg, init, update, view)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Graphql.Http
import Graphql.Operation exposing (RootMutation, RootQuery)
import Graphql.SelectionSet exposing (SelectionSet)
import Html exposing (Html, button, div, input, text)
import Html.Attributes exposing (class, id)
import Html.Events exposing (onClick, onInput)
import Language.Language exposing (Language)
import LondoGQL.Mutation as Mutation
import LondoGQL.Scalar exposing (Unit)
import Pages.Util.TriState as TriState exposing (TriState)
import RemoteData exposing (RemoteData)


type Msg
    = RequestToken
    | ChangeEmail String
    | GotResponse (RemoteData (Graphql.Http.Error Unit) Unit)


type alias Model =
    { email : String
    , language : Language
    , state : TriState
    , configuration : Configuration
    }


type alias Flags =
    { language : Language
    , configuration : Configuration
    }


updateEmail : Model -> String -> Model
updateEmail model email =
    { model | email = email }


updateState : Model -> TriState -> Model
updateState model state =
    { model | state = state }


init : Flags -> ( Model, Cmd Msg )
init flags =
    ( { email = "", language = flags.language, state = TriState.Initial, configuration = flags.configuration }, Cmd.none )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RequestToken ->
            ( model, makeRequest model )

        ChangeEmail string ->
            ( model |> flip updateEmail string, Cmd.none )

        GotResponse remoteData ->
            ( updateState model (TriState.fromRemoteData remoteData), Cmd.none )


view : Model -> Html Msg
view model =
    case model.state of
        TriState.Initial ->
            div [ id "registrationRequest" ]
                [ div [ id "registrationEmail" ] [ input [ onInput ChangeEmail ] [] ]
                , div [ id "registrationButton" ] [ button [ class "requestButton", onClick RequestToken ] [ text model.language.requestTokenForRegistration ] ]
                ]

        TriState.Success ->
            displayResponse model.language.tokenRequestSuccessful

        TriState.Failure ->
            displayResponse model.language.tokenRequestFailed


displayResponse : String -> Html Msg
displayResponse msg =
    div [ id "registrationResponse" ]
        [ text msg ]


requestTokenQuery : Model -> SelectionSet Unit RootMutation
requestTokenQuery model =
    Mutation.requestCreate { email = model.email }


makeRequest : Model -> Cmd Msg
makeRequest model =
    requestTokenQuery model
        |> Graphql.Http.mutationRequest model.configuration.graphQLEndpoint
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)
