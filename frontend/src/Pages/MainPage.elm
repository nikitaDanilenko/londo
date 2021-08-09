module Pages.MainPage exposing (main)

import Basics.Extra exposing (flip)
import Browser exposing (UrlRequest)
import Browser.Navigation as Nav
import Html exposing (Html, div, text)
import Language.Language as Language exposing (Language)
import Pages.Register.CreateRegistrationToken as CreateRegistrationToken
import Url exposing (Protocol(..), Url)
import Url.Parser as Parser exposing ((</>), Parser, s)


main : Program () Model Msg
main =
    Browser.application
        { init = init
        , onUrlChange = ChangedUrl
        , onUrlRequest = ClickedLink
        , subscriptions = \_ -> Sub.none
        , update = update
        , view = \model -> { title = titleFor model, body = [ view model ] }
        }


type alias Model =
    { key : Nav.Key
    , page : Page
    }


type Page
    = CreateRegistrationToken CreateRegistrationToken.Model
    | NotFound


type Msg
    = ClickedLink UrlRequest
    | ChangedUrl Url
    | CreateRegistrationTokenMsg CreateRegistrationToken.Msg


titleFor : Model -> String
titleFor _ =
    "Londo"


init : () -> Url -> Nav.Key -> ( Model, Cmd Msg )
init _ url key =
    stepTo url { page = NotFound, key = key }


view : Model -> Html Msg
view model =
    case model.page of
        CreateRegistrationToken createRegistrationToken ->
            Html.map CreateRegistrationTokenMsg (CreateRegistrationToken.view createRegistrationToken)

        NotFound ->
            div [] [ text "Page not found" ]


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        ClickedLink urlRequest ->
            case urlRequest of
                Browser.Internal url ->
                    ( model, Nav.pushUrl model.key (Url.toString url) )

                Browser.External href ->
                    ( model, Nav.load href )

        ChangedUrl url ->
            stepTo url model

        CreateRegistrationTokenMsg createRegistrationTokenMsg ->
            case model.page of
                CreateRegistrationToken createRegistrationToken ->
                    stepCreateRegistrationToken model (CreateRegistrationToken.update createRegistrationTokenMsg createRegistrationToken)

                _ ->
                    ( model, Cmd.none )


stepTo : Url -> Model -> ( Model, Cmd Msg )
stepTo url model =
    case Parser.parse routeParser (fragmentToPath url) of
        Just answer ->
            case answer of
                CreateRegistrationTokenRoute language ->
                    CreateRegistrationToken.init language |> stepCreateRegistrationToken model

        Nothing ->
            ( { model | page = NotFound }, Cmd.none )


stepCreateRegistrationToken : Model -> ( CreateRegistrationToken.Model, Cmd CreateRegistrationToken.Msg ) -> ( Model, Cmd Msg )
stepCreateRegistrationToken model ( createRegistrationToken, cmd ) =
    ( { model | page = CreateRegistrationToken createRegistrationToken }, Cmd.map CreateRegistrationTokenMsg cmd )


type Route
    = CreateRegistrationTokenRoute Language


routeParser : Parser (Route -> a) a
routeParser =
    let
        registrationParser =
            s "register" </> languageParser
    in
    Parser.oneOf
        [ route registrationParser CreateRegistrationTokenRoute
        ]


languageParser : Parser (Language -> a) a
languageParser =
    Parser.map Language.fromString (s "language" </> Parser.string)


fragmentToPath : Url -> Url
fragmentToPath url =
    { url | path = Maybe.withDefault "" url.fragment, fragment = Nothing }


route : Parser a b -> a -> Parser (b -> c) c
route =
    flip Parser.map
