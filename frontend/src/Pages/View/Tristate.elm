module Pages.View.Tristate exposing (Model, Msg(..), Status(..), createInitial, createMain, fold, foldMain, fromInitToMain, lenses, mapInitial, mapMain, toError, updateWith, view)

import Browser.Navigation
import Configuration exposing (Configuration)
import Graphql.Http
import Html exposing (Html, button, input, label, main_, p, table, td, text, tr)
import Html.Attributes exposing (disabled, for, id, value)
import Html.Events exposing (onClick)
import Language.Language as Language
import Maybe.Extra
import Monocle.Optional exposing (Optional)
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Util.HttpUtil as HttpUtil exposing (ErrorExplanation)


type alias Model main initial =
    { configuration : Configuration
    , errorLanguage : Language.ErrorHandling
    , status : Status main initial
    }


type alias ErrorState main =
    { errorExplanation : ErrorExplanation
    , previousMain : Maybe main
    }


type Status main initial
    = Initial initial
    | Main main
    | Error (ErrorState main)


createInitial : Configuration -> Language.ErrorHandling -> initial -> Model main initial
createInitial configuration language =
    Initial >> Model configuration language


createMain : Configuration -> Language.ErrorHandling -> main -> Model main initial
createMain configuration language =
    Main >> Model configuration language


fold :
    { onInitial : initial -> a
    , onMain : main -> a
    , onError : ErrorState main -> a
    }
    -> Model main initial
    -> a
fold fs t =
    case t.status of
        Initial initial ->
            fs.onInitial initial

        Main main ->
            fs.onMain main

        Error errorState ->
            fs.onError errorState


mapMain : (main -> main) -> Model main initial -> Model main initial
mapMain f t =
    fold
        { onInitial = always t
        , onMain = f >> createMain t.configuration t.errorLanguage
        , onError = always t
        }
        t


mapInitial : (initial -> initial) -> Model main initial -> Model main initial
mapInitial f t =
    fold
        { onInitial = f >> createInitial t.configuration t.errorLanguage
        , onMain = always t
        , onError = always t
        }
        t


foldMain : c -> (main -> c) -> Model main initial -> c
foldMain c f =
    fold
        { onInitial = always c
        , onMain = f
        , onError = always c
        }


lenses :
    { initial : Optional (Model main initial) initial
    , main : Optional (Model main initial) main
    , error : Optional (Model main initial) ErrorExplanation
    }
lenses =
    { initial =
        Optional asInitial
            (\b a ->
                case a.status of
                    Initial _ ->
                        Initial b |> Model a.configuration a.errorLanguage

                    _ ->
                        a
            )
    , main =
        Optional asMain
            (\b a ->
                case a.status of
                    Main _ ->
                        Main b |> Model a.configuration a.errorLanguage

                    _ ->
                        a
            )
    , error =
        Optional asError
            (\b a ->
                case a.status of
                    Error errorState ->
                        Error { errorState | errorExplanation = b } |> Model a.configuration a.errorLanguage

                    _ ->
                        a
            )
    }


asInitial : Model main initial -> Maybe initial
asInitial t =
    case t.status of
        Initial initial ->
            Just initial

        _ ->
            Nothing


asMain : Model main initial -> Maybe main
asMain t =
    case t.status of
        Main main ->
            Just main

        _ ->
            Nothing


asError : Model main initial -> Maybe ErrorExplanation
asError t =
    case t.status of
        Error errorState ->
            Just errorState.errorExplanation

        _ ->
            Nothing


fromInitToMain : (initial -> Maybe main) -> Model main initial -> Model main initial
fromInitToMain with t =
    t
        |> asInitial
        |> Maybe.andThen with
        |> Maybe.Extra.unwrap t (Main >> Model t.configuration t.errorLanguage)


toError : Model main initial -> Graphql.Http.Error a -> Model main initial
toError model error =
    let
        errorExplanation =
            error |> HttpUtil.graphQLErrorToExplanation
    in
    fold
        { onError = \errorState -> Model model.configuration model.errorLanguage (Error { errorState | errorExplanation = errorExplanation })
        , onMain = \main -> Model model.configuration model.errorLanguage (Error { errorExplanation = errorExplanation, previousMain = Just main })
        , onInitial = \_ -> Model model.configuration model.errorLanguage (Error { errorExplanation = errorExplanation, previousMain = Nothing })
        }
        model


view :
    { showLoginRedirect : Bool
    , viewMain : Configuration -> main -> List (Html msg)
    }
    -> Model main initial
    -> List (Html (Msg msg))
view ps t =
    case t.status of
        Initial _ ->
            [ main_ [] [ Links.loadingSymbol ] ]

        Main main ->
            ps.viewMain t.configuration main |> List.map (Html.map Logic)

        Error errorState ->
            let
                suggestionBlock =
                    if errorState.errorExplanation.possibleSolution |> String.isEmpty then
                        []

                    else
                        [ label [] [ text <| .suggestion <| .errorLanguage <| t ]
                        , label [] [ text <| errorState.errorExplanation.possibleSolution ]
                        ]

                redirectBlock =
                    t.configuration
                        |> Just
                        |> Maybe.Extra.filter (always ps.showLoginRedirect)
                        |> Maybe.Extra.unwrap []
                            (\configuration ->
                                [ Links.toLoginButtonWith
                                    { configuration = configuration
                                    , buttonText = t.errorLanguage.login
                                    , attributes = [ Style.classes.button.navigation ]
                                    }
                                ]
                            )

                retryBlock =
                    [ button
                        [ onClick HandleError, Style.classes.button.error ]
                        [ text <| .retry <| .errorLanguage <| t ]
                    ]
                        |> List.filter (always errorState.errorExplanation.suggestReload)
            in
            [ main_ [ Style.ids.error ]
                ([ p []
                    [ label [] [ text <| .errorOccurred <| .errorLanguage <| t ]
                    , label [] [ text <| errorState.errorExplanation.cause ]
                    ]
                 , p
                    []
                    suggestionBlock
                 ]
                    ++ retryBlock
                    ++ redirectBlock
                )
            ]


type Msg msg
    = Logic msg
    | HandleError


updateWith : (msg -> Model main initial -> ( Model main initial, Cmd msg )) -> Msg msg -> Model main initial -> ( Model main initial, Cmd (Msg msg) )
updateWith update msg model =
    case msg of
        HandleError ->
            model
                |> fold
                    { onInitial = \_ -> ( model, Browser.Navigation.reload )
                    , onMain = \_ -> ( model, Cmd.none )
                    , onError =
                        \errorState ->
                            errorState.previousMain
                                |> Maybe.Extra.unwrap
                                    ( model, Browser.Navigation.reload )
                                    (\main -> ( main |> Main |> Model model.configuration model.errorLanguage, Cmd.none ))
                    }

        Logic message ->
            let
                ( newModel, cmd ) =
                    update message model
            in
            ( newModel, Cmd.map Logic cmd )
