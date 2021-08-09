module Pages.Register.CreateRegistrationToken exposing (..)

import Basics.Extra exposing (flip)
import Graphql.Http
import Graphql.Operation exposing (RootMutation, RootQuery)
import Graphql.SelectionSet exposing (SelectionSet)
import Html exposing (Html, button, div, input, text)
import Html.Attributes exposing (class, id)
import Html.Events exposing (onClick, onInput)
import Language.Language exposing (Language)
import LondoGQL.Mutation as Mutation
import LondoGQL.Scalar exposing (Unit)
import RemoteData exposing (RemoteData)

type Msg
    = RequestToken
    | ChangeEmail String
    | GotResponse (RemoteData (Graphql.Http.Error Unit) Unit)


type alias Model =
    { email : String
    , language : Language
    }


updateEmail : Model -> String -> Model
updateEmail model email =
    { model | email = email }


init : Language -> (Model, Cmd Msg)
init language =
    ({ email = "", language = language }, Cmd.none)


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RequestToken ->
            ( model, makeRequest model )

        ChangeEmail string ->
            ( model |> flip updateEmail string, Cmd.none )

        GotResponse remoteData ->
            (model, Cmd.none)


view : Model -> Html Msg
view model =
    div [ id "registrationRequest" ]
        [ div [ id "registrationEmail" ] [ input [ onInput ChangeEmail ] [] ]
        , div [ id "registrationButton" ] [ button [ class "requestButton", onClick RequestToken ] [ text model.language.requestTokenForRegistration ] ]
        ]


requestTokenQuery : Model -> SelectionSet Unit RootMutation
requestTokenQuery model =
    Mutation.requestCreate { email = model.email }


makeRequest : Model -> Cmd Msg
makeRequest model =
    requestTokenQuery model
        |> Graphql.Http.mutationRequest "http://localhost:9000/graphql"
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)

