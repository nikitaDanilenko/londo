module Pages.Project.ProjectOverview exposing (..)

import Configuration exposing (Configuration)
import Html exposing (Html, button, div, text)
import Html.Attributes exposing (class, id)
import Html.Events exposing (onClick)
import Language.Language as Language exposing (Language)
import UUID exposing (UUID)


type alias Model =
    { token : String
    , projectOverviewLanguage : Language.ProjectOverview
    , configuration : Configuration
    }


type Msg
    = NewProject
    | SearchProject
    | EditProject UUID
    | Back


type alias Flags =
    { token : String
    , language : Language
    , configuration : Configuration
    }


init : Flags -> ( Model, Cmd Msg )
init flags =
    ( { token = flags.token
      , projectOverviewLanguage = flags.language.projectOverview
      , configuration = flags.configuration
      }
    , Cmd.none
    )


view : Model -> Html Msg
view model =
    div [ id "projectOverviewMain" ]
        [ div [ id "newProjectButton" ]
            [ button [ class "button", onClick NewProject ] [ text model.projectOverviewLanguage.newProject ] ]
        , div [ id "searchProjectButton" ]
            [ button [ class "button", onClick SearchProject ] [ text model.projectOverviewLanguage.searchProject ] ]
        ]



-- todo: Implement actions, the current implementation is just a placeholder


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        NewProject ->
            ( model, Cmd.none )

        SearchProject ->
            ( model, Cmd.none )

        EditProject uuid ->
            ( model, Cmd.none )

        Back ->
            ( model, Cmd.none )
