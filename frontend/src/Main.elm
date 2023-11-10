module Main exposing (main)

import Addresses.Frontend
import Basics.Extra exposing (flip)
import Browser exposing (UrlRequest)
import Browser.Navigation
import Configuration exposing (Configuration)
import Html exposing (Html, main_, text)
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Pages.DashboardEntries.Handler
import Pages.DashboardEntries.Page
import Pages.DashboardEntries.View
import Pages.Dashboards.Handler
import Pages.Dashboards.Page
import Pages.Dashboards.View
import Pages.Login.Handler
import Pages.Login.Page
import Pages.Login.View
import Pages.Overview.Handler
import Pages.Overview.Page
import Pages.Overview.View
import Pages.Projects.Handler
import Pages.Projects.Page
import Pages.Projects.View
import Pages.Registration.Confirm.Handler
import Pages.Registration.Confirm.Page
import Pages.Registration.Confirm.View
import Pages.Registration.Request.Handler
import Pages.Registration.Request.Page
import Pages.Registration.Request.View
import Pages.Settings.Handler
import Pages.Settings.Page
import Pages.Settings.View
import Pages.Statistics.Handler
import Pages.Statistics.Page
import Pages.Statistics.View
import Pages.Tasks.Handler
import Pages.Tasks.Page
import Pages.Tasks.View
import Ports
import Types.Auxiliary exposing (JWT, UserIdentifier)
import Types.Dashboard.Id
import Types.Project.Id exposing (Id(..))
import Url exposing (Protocol(..), Url)
import Url.Parser as Parser exposing (Parser)


main : Program Configuration Model Msg
main =
    Browser.application
        { init = init
        , onUrlChange = ChangedUrl
        , onUrlRequest = ClickedLink
        , subscriptions = subscriptions
        , update = update
        , view = \model -> { title = titleFor model, body = [ view model ] }
        }


subscriptions : Model -> Sub Msg
subscriptions _ =
    Sub.batch
        [ Ports.fetchToken FetchToken
        , Ports.deleteToken DeleteToken
        ]


type alias Model =
    { key : Browser.Navigation.Key
    , page : Page
    , configuration : Configuration
    , jwt : Maybe JWT
    , entryRoute : Maybe Route
    }


lenses :
    { jwt : Lens Model (Maybe JWT)
    , page : Lens Model Page
    , entryRoute : Lens Model (Maybe Route)
    }
lenses =
    { jwt = Lens .jwt (\b a -> { a | jwt = b })
    , page = Lens .page (\b a -> { a | page = b })
    , entryRoute = Lens .entryRoute (\b a -> { a | entryRoute = b })
    }


type Page
    = RequestRegistration Pages.Registration.Request.Page.Model
    | ConfirmRegistration Pages.Registration.Confirm.Page.Model
    | Login Pages.Login.Page.Model
    | Overview Pages.Overview.Page.Model
    | Projects Pages.Projects.Page.Model
    | Tasks Pages.Tasks.Page.Model
    | Dashboards Pages.Dashboards.Page.Model
    | DashboardEntries Pages.DashboardEntries.Page.Model
    | Statistics Pages.Statistics.Page.Model
    | Settings Pages.Settings.Page.Model
    | NotFound


type Msg
    = ClickedLink UrlRequest
    | ChangedUrl Url
    | FetchToken String
    | DeleteToken ()
    | RequestRegistrationMsg Pages.Registration.Request.Page.Msg
    | ConfirmRegistrationMsg Pages.Registration.Confirm.Page.Msg
    | LoginMsg Pages.Login.Page.Msg
    | OverviewMsg Pages.Overview.Page.Msg
    | ProjectsMsg Pages.Projects.Page.Msg
    | TasksMsg Pages.Tasks.Page.Msg
    | DashboardsMsg Pages.Dashboards.Page.Msg
    | DashboardEntriesMsg Pages.DashboardEntries.Page.Msg
    | StatisticsMsg Pages.Statistics.Page.Msg
    | SettingsMsg Pages.Settings.Page.Msg


titleFor : Model -> String
titleFor _ =
    "Londo"


init : Configuration -> Url -> Browser.Navigation.Key -> ( Model, Cmd Msg )
init configuration url key =
    ( { page = NotFound
      , key = key
      , configuration = configuration
      , jwt = Nothing
      , entryRoute = parsePage url
      }
    , Ports.doFetchToken ()
    )


view : Model -> Html Msg
view model =
    case model.page of
        RequestRegistration requestRegistration ->
            Html.map RequestRegistrationMsg (Pages.Registration.Request.View.view requestRegistration)

        ConfirmRegistration confirmRegistration ->
            Html.map ConfirmRegistrationMsg (Pages.Registration.Confirm.View.view confirmRegistration)

        Login login ->
            Html.map LoginMsg (Pages.Login.View.view login)

        Overview overview ->
            Html.map OverviewMsg (Pages.Overview.View.view overview)

        Projects projects ->
            Html.map ProjectsMsg (Pages.Projects.View.view projects)

        Tasks tasks ->
            Html.map TasksMsg (Pages.Tasks.View.view tasks)

        Dashboards dashboards ->
            Html.map DashboardsMsg (Pages.Dashboards.View.view dashboards)

        DashboardEntries dashboardEntries ->
            Html.map DashboardEntriesMsg (Pages.DashboardEntries.View.view dashboardEntries)

        Statistics statistics ->
            Html.map StatisticsMsg (Pages.Statistics.View.view statistics)

        Settings settings ->
            Html.map SettingsMsg (Pages.Settings.View.view settings)

        NotFound ->
            main_ [] [ text "Page not found" ]


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case ( msg, model.page ) of
        ( ClickedLink urlRequest, _ ) ->
            case urlRequest of
                Browser.Internal url ->
                    ( model, Browser.Navigation.pushUrl model.key (Url.toString url) )

                Browser.External href ->
                    ( model, Browser.Navigation.load href )

        ( ChangedUrl url, _ ) ->
            model
                |> lenses.entryRoute.set (url |> parsePage)
                |> followRoute

        ( FetchToken token, _ ) ->
            model
                |> lenses.jwt.set (Maybe.Extra.filter (String.isEmpty >> not) (Just token))
                |> followRoute

        --  todo: Remove command, otherwise there is an endless loop
        ( DeleteToken _, _ ) ->
            ( model |> lenses.jwt.set Nothing, Ports.doDeleteToken () )

        ( RequestRegistrationMsg requestRegistrationMsg, RequestRegistration requestRegistration ) ->
            stepThrough steps.requestRegistration model (Pages.Registration.Request.Handler.update requestRegistrationMsg requestRegistration)

        ( ConfirmRegistrationMsg confirmRegistrationMsg, ConfirmRegistration confirmRegistration ) ->
            stepThrough steps.confirmRegistration model (Pages.Registration.Confirm.Handler.update confirmRegistrationMsg confirmRegistration)

        ( LoginMsg loginMsg, Login login ) ->
            stepThrough steps.login model (Pages.Login.Handler.update loginMsg login)

        ( OverviewMsg overviewMsg, Overview overview ) ->
            stepThrough steps.overview model (Pages.Overview.Handler.update overviewMsg overview)

        ( ProjectsMsg projectsMsg, Projects projects ) ->
            stepThrough steps.projects model (Pages.Projects.Handler.update projectsMsg projects)

        ( TasksMsg tasksMsg, Tasks tasks ) ->
            stepThrough steps.tasks model (Pages.Tasks.Handler.update tasksMsg tasks)

        ( DashboardsMsg dashboardsMsg, Dashboards dashboards ) ->
            stepThrough steps.dashboards model (Pages.Dashboards.Handler.update dashboardsMsg dashboards)

        ( DashboardEntriesMsg dashboardEntriesMsg, DashboardEntries dashboardEntries ) ->
            stepThrough steps.dashboardEntries model (Pages.DashboardEntries.Handler.update dashboardEntriesMsg dashboardEntries)

        ( StatisticsMsg statisticsMsg, Statistics statistics ) ->
            stepThrough steps.statistics model (Pages.Statistics.Handler.update statisticsMsg statistics)

        ( SettingsMsg settingsMsg, Settings settings ) ->
            stepThrough steps.settings model (Pages.Settings.Handler.update settingsMsg settings)

        _ ->
            ( model, Cmd.none )


type alias StepParameters model msg =
    { page : model -> Page
    , message : msg -> Msg
    }


steps :
    { login : StepParameters Pages.Login.Page.Model Pages.Login.Page.Msg
    , overview : StepParameters Pages.Overview.Page.Model Pages.Overview.Page.Msg
    , projects : StepParameters Pages.Projects.Page.Model Pages.Projects.Page.Msg
    , tasks : StepParameters Pages.Tasks.Page.Model Pages.Tasks.Page.Msg
    , dashboards : StepParameters Pages.Dashboards.Page.Model Pages.Dashboards.Page.Msg
    , dashboardEntries : StepParameters Pages.DashboardEntries.Page.Model Pages.DashboardEntries.Page.Msg
    , statistics : StepParameters Pages.Statistics.Page.Model Pages.Statistics.Page.Msg
    , requestRegistration : StepParameters Pages.Registration.Request.Page.Model Pages.Registration.Request.Page.Msg
    , confirmRegistration : StepParameters Pages.Registration.Confirm.Page.Model Pages.Registration.Confirm.Page.Msg
    , settings : StepParameters Pages.Settings.Page.Model Pages.Settings.Page.Msg

    --, deletion : StepParameters Pages.Deletion.Page.Model Pages.Deletion.Page.Msg
    --, requestRecovery : StepParameters Pages.Recovery.Request.Page.Model Pages.Recovery.Request.Page.Msg
    --, confirmRecovery : StepParameters Pages.Recovery.Confirm.Page.Model Pages.Recovery.Confirm.Page.Msg
    }
steps =
    { login = StepParameters Login LoginMsg
    , overview = StepParameters Overview OverviewMsg
    , projects = StepParameters Projects ProjectsMsg
    , tasks = StepParameters Tasks TasksMsg
    , dashboards = StepParameters Dashboards DashboardsMsg
    , dashboardEntries = StepParameters DashboardEntries DashboardEntriesMsg
    , statistics = StepParameters Statistics StatisticsMsg
    , requestRegistration = StepParameters RequestRegistration RequestRegistrationMsg
    , confirmRegistration = StepParameters ConfirmRegistration ConfirmRegistrationMsg
    , settings = StepParameters Settings SettingsMsg

    --, deletion = StepParameters Deletion DeletionMsg
    --, requestRecovery = StepParameters RequestRecovery RequestRecoveryMsg
    --, confirmRecovery = StepParameters ConfirmRecovery ConfirmRecoveryMsg
    }


stepThrough : { page : model -> Page, message : msg -> Msg } -> Model -> ( model, Cmd msg ) -> ( Model, Cmd Msg )
stepThrough ps model ( subModel, cmd ) =
    ( { model | page = ps.page subModel }, Cmd.map ps.message cmd )


type Route
    = RequestRegistrationRoute
    | ConfirmRegistrationRoute UserIdentifier JWT
    | LoginRoute
    | OverviewRoute
    | ProjectsRoute
    | TasksRoute Types.Project.Id.Id
    | DashboardsRoute
    | DashboardEntriesRoute Types.Dashboard.Id.Id
    | StatisticsRoute Types.Dashboard.Id.Id
    | SettingsRoute


plainRouteParser : Parser (Route -> a) a
plainRouteParser =
    Parser.oneOf
        [ route Addresses.Frontend.requestRegistration.parser RequestRegistrationRoute
        , route Addresses.Frontend.confirmRegistration.parser ConfirmRegistrationRoute
        , route Addresses.Frontend.login.parser LoginRoute
        , route Addresses.Frontend.overview.parser OverviewRoute
        , route Addresses.Frontend.projects.parser ProjectsRoute
        , route Addresses.Frontend.tasks.parser TasksRoute
        , route Addresses.Frontend.dashboards.parser DashboardsRoute
        , route Addresses.Frontend.dashboardEntries.parser DashboardEntriesRoute
        , route Addresses.Frontend.statistics.parser StatisticsRoute
        , route Addresses.Frontend.settings.parser SettingsRoute
        ]


parsePage : Url -> Maybe Route
parsePage =
    fragmentToPath >> Parser.parse plainRouteParser


{-| Todo: Rethink the structure - the matching is inelegant, error prone, and contains duplication.
-}
followRoute : Model -> ( Model, Cmd Msg )
followRoute model =
    let
        authorizedAccessWith jwt =
            { configuration = model.configuration
            , jwt = jwt
            }
    in
    case model.entryRoute of
        Nothing ->
            ( { model | page = NotFound }, Cmd.none )

        Just entryRoute ->
            case ( entryRoute, model.jwt ) of
                ( RequestRegistrationRoute, _ ) ->
                    Pages.Registration.Request.Handler.init { configuration = model.configuration }
                        |> stepThrough steps.requestRegistration model

                ( ConfirmRegistrationRoute userIdentifier registrationJWT, _ ) ->
                    Pages.Registration.Confirm.Handler.init
                        { configuration = model.configuration
                        , userIdentifier = userIdentifier
                        , registrationJWT = registrationJWT
                        }
                        |> stepThrough steps.confirmRegistration model

                ( LoginRoute, _ ) ->
                    Pages.Login.Handler.init { configuration = model.configuration }
                        |> stepThrough steps.login model

                ( OverviewRoute, Just _ ) ->
                    Pages.Overview.Handler.init { configuration = model.configuration }
                        |> stepThrough steps.overview model

                ( ProjectsRoute, Just userJWT ) ->
                    Pages.Projects.Handler.init
                        { authorizedAccess = authorizedAccessWith userJWT
                        }
                        |> stepThrough steps.projects model

                ( TasksRoute projectId, Just userJWT ) ->
                    Pages.Tasks.Handler.init
                        { authorizedAccess = authorizedAccessWith userJWT
                        , projectId = projectId
                        }
                        |> stepThrough steps.tasks model

                ( DashboardsRoute, Just userJWT ) ->
                    Pages.Dashboards.Handler.init
                        { authorizedAccess = authorizedAccessWith userJWT
                        }
                        |> stepThrough steps.dashboards model

                ( DashboardEntriesRoute dashboardId, Just userJWT ) ->
                    Pages.DashboardEntries.Handler.init
                        { authorizedAccess = authorizedAccessWith userJWT
                        , dashboardId = dashboardId
                        }
                        |> stepThrough steps.dashboardEntries model

                ( StatisticsRoute dashboardId, Just userJWT ) ->
                    Pages.Statistics.Handler.init
                        { authorizedAccess = authorizedAccessWith userJWT
                        , dashboardId = dashboardId
                        }
                        |> stepThrough steps.statistics model

                ( SettingsRoute, Just userJWT ) ->
                    Pages.Settings.Handler.init
                        { authorizedAccess = authorizedAccessWith userJWT
                        }
                        |> stepThrough steps.settings model

                _ ->
                    Pages.Login.Handler.init { configuration = model.configuration }
                        |> stepThrough steps.login model


fragmentToPath : Url -> Url
fragmentToPath url =
    { url | path = Maybe.withDefault "" url.fragment, fragment = Nothing }


route : Parser a b -> a -> Parser (b -> c) c
route =
    flip Parser.map
