module Pages.View.Tristate exposing (Model, Msg(..), Status(..), createInitial, createMain, fold, foldMain, fromInitToMain, lenses, mapInitial, mapMain, toError, updateWith, view)

import Browser.Navigation
import Configuration exposing (Configuration)
import Graphql.Http
import Html exposing (Html, button, main_, table, td, text, tr)
import Html.Events exposing (onClick)
import Maybe.Extra
import Monocle.Optional exposing (Optional)
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Util.HttpUtil as HttpUtil exposing (ErrorExplanation)


type alias Model main initial =
    { configuration : Configuration
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


createInitial : Configuration -> initial -> Model main initial
createInitial configuration =
    Initial >> Model configuration


createMain : Configuration -> main -> Model main initial
createMain configuration =
    Main >> Model configuration


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
        , onMain = f >> createMain t.configuration
        , onError = always t
        }
        t


mapInitial : (initial -> initial) -> Model main initial -> Model main initial
mapInitial f t =
    fold
        { onInitial = f >> createInitial t.configuration
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
                        Initial b |> Model a.configuration

                    _ ->
                        a
            )
    , main =
        Optional asMain
            (\b a ->
                case a.status of
                    Main _ ->
                        Main b |> Model a.configuration

                    _ ->
                        a
            )
    , error =
        Optional asError
            (\b a ->
                case a.status of
                    Error errorState ->
                        Error { errorState | errorExplanation = b } |> Model a.configuration

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
        |> Maybe.Extra.unwrap t (Main >> Model t.configuration)


toError : Model main initial -> Graphql.Http.Error a -> Model main initial
toError model error =
    let
        errorExplanation =
            error |> HttpUtil.graphQLErrorToExplanation
    in
    fold
        { onError = \errorState -> Model model.configuration (Error { errorState | errorExplanation = errorExplanation })
        , onMain = \main -> Model model.configuration (Error { errorExplanation = errorExplanation, previousMain = Just main })
        , onInitial = \_ -> Model model.configuration (Error { errorExplanation = errorExplanation, previousMain = Nothing })
        }
        model


view :
    { showLoginRedirect : Bool
    , viewMain : Configuration -> main -> Html msg
    }
    -> Model main initial
    -> Html (Msg msg)
view ps t =
    case t.status of
        Initial _ ->
            main_ [] [ Links.loadingSymbol ]

        Main main ->
            ps.viewMain t.configuration main |> Html.map Logic

        Error errorState ->
            let
                solutionBlock =
                    if errorState.errorExplanation.possibleSolution |> String.isEmpty then
                        []

                    else
                        [ td [] [ text "Try the following:" ] --todo: use language elements
                        , td [] [ text <| errorState.errorExplanation.possibleSolution ]
                        ]

                redirectRow =
                    t.configuration
                        |> Just
                        |> Maybe.Extra.filter (always ps.showLoginRedirect)
                        |> Maybe.Extra.unwrap []
                            (\configuration ->
                                [ tr []
                                    [ td []
                                        [ Links.toLoginButtonWith
                                            { configuration = configuration
                                            , buttonText = "Login" -- todo: Use language elements
                                            , attributes = [ Style.classes.button.error ]
                                            }
                                        ]
                                    ]
                                ]
                            )

                reloadRow =
                    [ tr []
                        [ td []
                            [ button [ onClick HandleError, Style.classes.button.error ] [ text "Retry" ] -- todo: Use language elements
                            ]
                        ]
                    ]
                        |> List.filter (always errorState.errorExplanation.suggestReload)
            in
            table
                [ Style.ids.error ]
                ([ tr []
                    [ td [] [ text "An error occurred:" ] -- todo: Use language elements
                    , td [] [ text <| errorState.errorExplanation.cause ]
                    ]
                 , tr [] solutionBlock
                 ]
                    ++ redirectRow
                    ++ reloadRow
                )


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
                                    (\main -> ( main |> Main |> Model model.configuration, Cmd.none ))
                    }

        Logic message ->
            let
                ( newModel, cmd ) =
                    update message model
            in
            ( newModel, Cmd.map Logic cmd )
