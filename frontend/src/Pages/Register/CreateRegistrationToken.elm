module Pages.Register.CreateRegistrationToken exposing (..)

import Basics.Extra exposing (flip)
import Html exposing (Html, button, div, input, text)
import Html.Attributes exposing (class, id)
import Html.Events exposing (onClick, onInput)
import Language.Language exposing (Language)


type Msg
    = RequestToken
    | ChangeEmail String


type alias Model =
    { email : String
    , language : Language
    }


updateEmail : Model -> String -> Model
updateEmail model email =
    { model | email = email }


init : Language -> Model
init language =
    { email = "", language = language }


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        RequestToken ->
            ( model, Cmd.none )

        ChangeEmail string ->
            ( model |> flip updateEmail string, Cmd.none )


view : Model -> Html Msg
view model =
    div [ id "registrationRequest" ]
        [ div [ id "registrationEmail" ] [ input [ onInput ChangeEmail ] [] ]
        , div [ id "registrationButton" ] [ button [ class "requestButton", onClick RequestToken ] [ text model.language.requestTokenForRegistration ] ]
        ]

--requestToken : String -> Cmd Msg