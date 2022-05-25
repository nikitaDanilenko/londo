module Pages.Overview.Overview exposing (Flags, Model, Msg, init, update, view)

import Browser.Navigation as Navigation
import Configuration exposing (Configuration)
import Html exposing (Html, button, div, text)
import Html.Attributes exposing (class, id)
import Html.Events exposing (onClick)
import Language.Language as Language exposing (Language)
import Url.Builder as UrlBuilder


type alias Model =
    { token : String
    , language : Language.Overview
    , configuration : Configuration
    }


type Msg
    = Dashboards
    | Projects
    | Settings


type alias Flags =
    { token : String
    , language : Language
    , configuration : Configuration
    }


init : Flags -> ( Model, Cmd Msg )
init flags =
    ( { token = flags.token
      , language = flags.language.overview
      , configuration = flags.configuration
      }
    , Cmd.none
    )


view : Model -> Html Msg
view model =
    div [ id "overviewMain" ]
        [ div [ id "dashboardsButton" ]
            [ button [ class "button", onClick Dashboards ] [ text model.language.dashboards ] ]
        , div [ id "projectsButton" ]
            [ button [ class "button", onClick Projects ] [ text model.language.projects ] ]
        , div [ id "settingsButton" ]
            [ button [ class "button", onClick Settings ] [ text model.language.settings ] ]
        ]


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    let
        subFolder =
            case msg of
                Dashboards ->
                    model.configuration.subFolders.dashboards

                Projects ->
                    model.configuration.subFolders.projects

                Settings ->
                    model.configuration.subFolders.settings

        link =
            UrlBuilder.relative
                [ model.configuration.mainPageURL
                , "#"
                , subFolder
                , "token"
                , model.token
                ]
                []
    in
    ( model, Navigation.load link )
