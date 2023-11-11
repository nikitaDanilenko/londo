module Pages.Recovery.Request.View exposing (view)

import Configuration exposing (Configuration)
import Html exposing (Html, button, form, h1, h2, input, label, section, text)
import Html.Attributes exposing (disabled, for, id, required, type_, value)
import Html.Events exposing (onClick, onInput, onSubmit)
import Pages.Recovery.Request.Page as Page
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate


view : Page.Model -> List (Html Page.Msg)
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Configuration -> Page.Main -> List (Html Page.LogicMsg)
viewMain configuration main =
    ViewUtil.viewMainWith
        { configuration = configuration
        , currentPage = Nothing
        , showNavigation = False
        , id = Style.ids.accountRecovery
        }
    <|
        case main.mode of
            Page.Initial ->
                viewInitial main

            Page.Requesting ->
                viewRequesting main

            Page.Requested ->
                viewRequested main.language configuration


viewInitial : Page.Main -> List (Html Page.LogicMsg)
viewInitial =
    searchComponents


viewRequesting : Page.Main -> List (Html Page.LogicMsg)
viewRequesting main =
    let
        message =
            if List.isEmpty main.users then
                .noAccountFound

            else
                .multipleAccountsFound

        remainder =
            [ section []
                (h2 [] [ text <| message <| .language <| main ]
                    :: List.map chooseUser main.users
                )
            ]

        chooseUser user =
            button
                [ onClick (Page.RequestRecovery user.id)
                , Style.classes.button.navigation
                ]
                [ text user.nickname ]
    in
    searchComponents main ++ remainder


viewRequested : Page.Language -> Configuration -> List (Html Page.LogicMsg)
viewRequested language configuration =
    [ text <| .requestSuccessful <| language
    , Links.toLoginButton
        { configuration = configuration
        , buttonText = language |> .mainPage
        }
    ]


searchComponents : Page.Main -> List (Html Page.LogicMsg)
searchComponents main =
    let
        isValid =
            main.searchString |> String.isEmpty |> not

        identifier =
            "identifier"
    in
    [ h1 [] [ text <| .recovery <| .language <| main ]
    , form
        [ onSubmit Page.Find ]
        [ label [ for identifier ] [ text <| .identifier <| .language <| main ]
        , input
            [ onInput Page.SetSearchString
            , Style.classes.editable
            , value <| main.searchString
            , id <| identifier
            , type_ "text"
            , required True
            ]
            []
        , button
            [ Style.classes.button.confirm
            , disabled <| not <| isValid
            , type_ "submit"
            ]
            [ text <| .find <| .language <| main ]
        ]
    ]
