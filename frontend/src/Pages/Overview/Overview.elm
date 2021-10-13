module Pages.Overview.Overview exposing (..)

import Configuration exposing (Configuration)
import Html exposing (Html, button, div, text)
import Html.Attributes exposing (class, id)
import Html.Events exposing (onClick)
import Language.Language as Language exposing (Language)
import UUID exposing (UUID)


type alias Model =
    { userId : UUID
    , overviewLanguage : Language.Overview
    , configuration : Configuration
    }


type Msg
    = Dashboards
    | Projects
    | Settings


type alias Flags =
    { userId : UUID
    , language : Language
    , configuration : Configuration
    }


init : Flags -> ( Model, Cmd Msg )
init flags =
    ( { userId = flags.userId
      , overviewLanguage = flags.language.overview
      , configuration = flags.configuration
      }
    , Cmd.none
    )


view : Model -> Html Msg
view model =
    div [ id "overviewMain" ]
        [ div [ id "fetchButton" ]
            [ button [ class "button", onClick Dashboards ] [ text model.overviewLanguage.dashboards ] ]
        , div [ id "fetchButton" ]
            [ button [ class "button", onClick Projects ] [ text model.overviewLanguage.projects ] ]
        , div [ id "fetchButton" ]
            [ button [ class "button", onClick Settings ] [ text model.overviewLanguage.settings ] ]
        ]



-- todo: Implement actions, the current implementation is just a placeholder


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        Dashboards ->
            ( model, Cmd.none )

        Projects ->
            ( model, Cmd.none )

        Settings ->
            ( model, Cmd.none )
