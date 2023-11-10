module Pages.Login.View exposing (..)

import Addresses.Frontend
import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Html, button, form, h1, input, label, main_, text)
import Html.Attributes exposing (autocomplete, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Monocle.Lens as Lens
import Pages.Login.Page as Page
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Pages.View.Tristate as Tristate
import Types.User.Login


view : Page.Model -> Html Page.Msg
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = False
        }


viewMain : Configuration -> Page.Main -> Html Page.LogicMsg
viewMain configuration main =
    let
        username =
            "username"

        password =
            "password"

        keepLoggedIn =
            "keep-logged-in"
    in
    main_ [ Style.ids.login ]
        --todo: This should be set via a language component
        [ h1 [] [ text "Londo" ]
        , form
            []
            [ label
                [ for username ]
                [ text <| main.language.nickname ]
            , input
                [ autocomplete True
                , value <| Types.User.Login.lenses.nickname.get <| main.credentials
                , onInput <|
                    Page.SetCredentials
                        << flip Types.User.Login.lenses.nickname.set main.credentials
                , onEnter Page.Login
                , Style.classes.editable
                , id username
                , type_ "text"
                ]
                []
            , label
                [ for password ]
                [ text <| main.language.password ]
            , input
                [ type_ "password"
                , autocomplete True
                , onInput <|
                    Page.SetCredentials
                        << flip Types.User.Login.lenses.password.set main.credentials
                , onEnter Page.Login
                , Style.classes.editable
                , id password
                ]
                []
            , label
                [ for keepLoggedIn
                ]
                [ text <| main.language.keepMeLoggedIn
                ]
            , input
                [ type_ "checkbox"
                , onClick <|
                    Page.SetCredentials <|
                        Lens.modify Types.User.Login.lenses.isValidityUnrestricted not main.credentials
                , onEnter Page.Login
                , Style.classes.editable
                , id keepLoggedIn
                ]
                []
            , button [ onClick Page.Login, Style.classes.button.confirm ] [ text <| main.language.login ]
            , Links.linkButton
                { url = Links.frontendPage configuration <| Addresses.Frontend.requestRegistration.address ()
                , attributes = [ Style.classes.button.navigation ]
                , linkText = main.language.createAccount
                }
            , Links.linkButton
                { url = Links.frontendPage configuration <| Addresses.Frontend.requestRecovery.address ()
                , attributes = [ Style.classes.button.navigation ]
                , linkText = main.language.recoverAccount
                }
            ]
        ]
