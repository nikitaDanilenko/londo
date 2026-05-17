module Pages.Deletion.View exposing (view)

import Html exposing (Html, button, form, h1, input, label, text)
import Html.Attributes exposing (disabled, for, type_, value)
import Html.Events exposing (onClick)
import Pages.Deletion.Page as Page
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


viewMain : Page.Main -> List (Html Page.LogicMsg)
viewMain main =
    ViewUtil.viewMainWith
        { currentPage = Nothing
        , showNavigation = False
        , id = Style.ids.accountDeletion
        }
    <|
        case main.mode of
            Page.Checking ->
                viewChecking main

            Page.Confirmed ->
                viewConfirmed main.language


viewChecking : Page.Main -> List (Html Page.LogicMsg)
viewChecking main =
    let
        nickname =
            "nickname"

        email =
            "email"
    in
    [ h1 [] [ text <| .confirmDeletion <| .language <| main ]
    , form []
        [ label [ for nickname ] [ text <| .nickname <| .language <| main ]
        , input [ type_ "text", disabled True, value <| .nickname <| .userIdentifier <| main ]
            []
        , label [ for email ] [ text <| .email <| .language <| main ]
        , input [ type_ "text", disabled True, value <| .email <| .userIdentifier <| main ]
            []
        , button
            [ onClick <| Page.Confirm
            , Style.classes.button.delete
            , type_ "button"
            ]
            [ text <| .delete <| .language <| main ]
        , Links.toLoginButton
            { buttonText = main |> .language |> .cancel
            }
        ]
    ]


viewConfirmed : Page.Language -> List (Html Page.LogicMsg)
viewConfirmed language =
    [ text <| .deletionSuccessful <| language
    , Links.toLoginButton
        { buttonText = language |> .mainPage
        }
    ]
