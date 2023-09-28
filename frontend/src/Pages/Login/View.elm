module Pages.Login.View exposing (..)

import Addresses.Frontend
import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Html, button, input, table, tbody, td, text, tr)
import Html.Attributes exposing (autocomplete, colspan, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Monocle.Lens as Lens
import Pages.Login.Page as Page
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Types.Credentials as Credentials


view : Page.Model -> Html Page.Msg
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = False
        }


viewMain : Configuration -> Page.Main -> Html Page.LogicMsg
viewMain configuration main =
    ViewUtil.viewMainWith
        { configuration = configuration
        , currentPage = Just ViewUtil.Login
        , showNavigation = False
        }
    <|
        [ table []
            -- todo: Reconsider the use of a table - a form may be a better choice
            [ tbody []
                [ tr []
                    [ td [] [ text <| main.language.nickname ]
                    , td []
                        [ input
                            [ autocomplete True
                            , value <| Credentials.lenses.nickname.get <| main.credentials
                            , onInput <|
                                Page.SetCredentials
                                    << flip Credentials.lenses.nickname.set main.credentials
                            , onEnter Page.Login
                            , Style.classes.editable
                            ]
                            []
                        ]
                    ]
                , tr []
                    [ td [] [ text <| main.language.password ]
                    , td []
                        [ input
                            [ type_ "password"
                            , autocomplete True
                            , onInput <|
                                Page.SetCredentials
                                    << flip Credentials.lenses.password.set main.credentials
                            , onEnter Page.Login
                            , Style.classes.editable
                            ]
                            []
                        ]
                    ]
                , tr []
                    [ td [] [ text <| main.language.keepMeLoggedIn ]
                    , td []
                        [ input
                            [ type_ "checkbox"
                            , onClick <|
                                Page.SetCredentials <|
                                    Lens.modify Credentials.lenses.isValidityUnrestricted not main.credentials
                            , onEnter Page.Login
                            , Style.classes.editable
                            ]
                            []
                        ]
                    ]
                , tr []
                    [ td [ colspan 2 ]
                        [ button [ onClick Page.Login, Style.classes.button.confirm ] [ text <| main.language.login ]
                        ]
                    ]
                , tr []
                    [ td [ colspan 2 ]
                        [ Links.linkButton
                            { url = Links.frontendPage configuration <| Addresses.Frontend.requestRegistration.address ()
                            , attributes = [ Style.classes.button.navigation ]
                            , children = [ text <| main.language.createAccount ]
                            }
                        ]
                    ]
                , tr []
                    [ td [ colspan 2 ]
                        [ Links.linkButton
                            { url = Links.frontendPage configuration <| Addresses.Frontend.requestRecovery.address ()
                            , attributes = [ Style.classes.button.navigation ]
                            , children = [ text <| main.language.recoverAccount ]
                            }
                        ]
                    ]
                ]
            ]
        ]
