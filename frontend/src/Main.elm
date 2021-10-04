module Main exposing (main)

import Basics.Extra exposing (flip)
import Browser exposing (UrlRequest)
import Browser.Navigation as Nav
import Configuration exposing (Configuration)
import Html exposing (Html, div, text)
import Language.Language as Language exposing (Language)
import Pages.Login.Login as Login
import Pages.Register.CreateNewUser as CreateNewUser
import Pages.Register.CreateRegistrationToken as CreateRegistrationToken
import Url exposing (Protocol(..), Url)
import Url.Parser as Parser exposing ((</>), Parser, s)


main : Program Configuration Model Msg
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
    , configuration : Configuration
    }


type Page
    = CreateRegistrationToken CreateRegistrationToken.Model
    | CreateNewUser CreateNewUser.Model
    | Login Login.Model
    | NotFound


type Msg
    = ClickedLink UrlRequest
    | ChangedUrl Url
    | CreateRegistrationTokenMsg CreateRegistrationToken.Msg
    | CreateNewUserMsg CreateNewUser.Msg
    | LoginMsg Login.Msg


titleFor : Model -> String
titleFor _ =
    "Londo"


init : Configuration -> Url -> Nav.Key -> ( Model, Cmd Msg )
init configuration url key =
    stepTo url
        { page = NotFound
        , key = key
        , configuration = configuration
        }


view : Model -> Html Msg
view model =
    case model.page of
        CreateRegistrationToken createRegistrationToken ->
            Html.map CreateRegistrationTokenMsg (CreateRegistrationToken.view createRegistrationToken)

        CreateNewUser createNewUser ->
            Html.map CreateNewUserMsg (CreateNewUser.view createNewUser)

        Login login ->
            Html.map LoginMsg (Login.view login)

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

        CreateNewUserMsg createNewUserMsg ->
            case model.page of
                CreateNewUser createNewUser ->
                    stepCreateNewUser model (CreateNewUser.update createNewUserMsg createNewUser)

                _ ->
                    ( model, Cmd.none )

        LoginMsg loginMsg ->
            case model.page of
                Login login ->
                    stepLogin model (Login.update loginMsg login)

                _ ->
                    ( model, Cmd.none )


stepTo : Url -> Model -> ( Model, Cmd Msg )
stepTo url model =
    case Parser.parse (routeParser model.configuration) (fragmentToPath url) of
        Just answer ->
            case answer of
                CreateRegistrationTokenRoute flags ->
                    CreateRegistrationToken.init flags |> stepCreateRegistrationToken model

                CreateNewUserRoute flags ->
                    CreateNewUser.init flags |> stepCreateNewUser model

        Nothing ->
            ( { model | page = NotFound }, Cmd.none )


stepCreateRegistrationToken : Model -> ( CreateRegistrationToken.Model, Cmd CreateRegistrationToken.Msg ) -> ( Model, Cmd Msg )
stepCreateRegistrationToken model ( createRegistrationToken, cmd ) =
    ( { model | page = CreateRegistrationToken createRegistrationToken }, Cmd.map CreateRegistrationTokenMsg cmd )


stepCreateNewUser : Model -> ( CreateNewUser.Model, Cmd CreateNewUser.Msg ) -> ( Model, Cmd Msg )
stepCreateNewUser model ( createNewUser, cmd ) =
    ( { model | page = CreateNewUser createNewUser }, Cmd.map CreateNewUserMsg cmd )


stepLogin : Model -> ( Login.Model, Cmd Login.Msg ) -> ( Model, Cmd Msg )
stepLogin model ( login, cmd ) =
    ( { model | page = Login login }, Cmd.map LoginMsg cmd )


type Route
    = CreateRegistrationTokenRoute CreateRegistrationToken.Flags
    | CreateNewUserRoute CreateNewUser.Flags


routeParser : Configuration -> Parser (Route -> a) a
routeParser configuration =
    let
        registrationTokenParser =
            Parser.map (\l -> { language = l, configuration = configuration }) (s configuration.subFolders.register </> languageParser)

        createNewUserParser =
            Parser.map
                (\email token language ->
                    { email = email
                    , token = token
                    , language = language
                    , configuration = configuration
                    }
                )
                (s configuration.subFolders.register </> s "email" </> Parser.string </> s "token" </> Parser.string </> languageParser)
    in
    Parser.oneOf
        [ route registrationTokenParser CreateRegistrationTokenRoute
        , route createNewUserParser CreateNewUserRoute
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
