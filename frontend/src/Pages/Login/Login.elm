module Pages.Login.Login exposing (..)

import Graphql.Http
import Html exposing (Html, div)
import Language.Language as Language
import Pages.Util.TriState exposing (TriState(..))
import RemoteData exposing (RemoteData)


type alias Model =
    { user : String
    , password : String
    , loginLanguage : Language.Login
    , state : TriState
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


type Msg
    = SetUser String
    | SetPassword String
    | Login
    | GotResponse (RemoteData (Graphql.Http.Error String) String)


init : Language.Login -> Model
init language =
    { user = ""
    , password = ""
    , loginLanguage = language
    , state = Initial
    }


view : Model -> Html Msg
view model =
    div [] []


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    ( model, Cmd.none )
