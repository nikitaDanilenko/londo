module Pages.Register.CreateRegistrationToken exposing (Model, Msg, init, update, view)

import Basics.Extra exposing (flip)
import Configuration
import Graphql.Http
import Graphql.Operation exposing (RootMutation, RootQuery)
import Graphql.SelectionSet exposing (SelectionSet)
import Html exposing (Html, button, div, input, text)
import Html.Attributes exposing (class, id)
import Html.Events exposing (onClick, onInput)
import Language.Language exposing (Language)
import LondoGQL.Mutation as Mutation
import LondoGQL.Scalar exposing (Unit)
import Maybe.Extra exposing (unwrap)
import RemoteData exposing (RemoteData)


type Msg
    = RequestToken
    | ChangeEmail String
    | GotResponse (RemoteData (Graphql.Http.Error Unit) Unit)


type State
    = Initial
    | Success
    | Failure


type alias Model =
    { email : String
    , language : Language
    , state : State
    }


updateEmail : Model -> String -> Model
updateEmail model email =
    { model | email = email }


updateState : Model -> State -> Model
updateState model state =
    { model | state = state }


init : Language -> ( Model, Cmd Msg )
init language =
    ( { email = "", language = language, state = Initial }, Cmd.none )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RequestToken ->
            ( model, makeRequest model )

        ChangeEmail string ->
            ( model |> flip updateEmail string, Cmd.none )

        GotResponse remoteData ->
            let
                state =
                    remoteData |> RemoteData.toMaybe |> unwrap Failure (\_ -> Success)
            in
            ( updateState model state, Cmd.none )


view : Model -> Html Msg
view model =
    case model.state of
        Initial ->
            div [ id "registrationRequest" ]
                [ div [ id "registrationEmail" ] [ input [ onInput ChangeEmail ] [] ]
                , div [ id "registrationButton" ] [ button [ class "requestButton", onClick RequestToken ] [ text model.language.requestTokenForRegistration ] ]
                ]

        Success ->
            displayResponse model.language.tokenRequestSuccessful

        Failure ->
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
        |> Graphql.Http.mutationRequest Configuration.graphQLEndpoint
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)
