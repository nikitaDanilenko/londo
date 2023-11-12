module Pages.Recovery.Confirm.View exposing (view)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Html, button, form, h1, input, label, text)
import Html.Attributes exposing (disabled, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Maybe.Extra
import Pages.Recovery.Confirm.Page as Page
import Pages.Util.Links as Links
import Pages.Util.PasswordInput as PasswordInput
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Util.MaybeUtil as MaybeUtil


view : Page.Model -> List (Html Page.Msg)
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Configuration -> Page.Main -> List (Html Page.LogicMsg)
viewMain configuration model =
    ViewUtil.viewMainWith
        { configuration = configuration
        , currentPage = Nothing
        , showNavigation = False
        , id = Style.ids.accountRecovery
        }
    <|
        case model.mode of
            Page.Resetting ->
                viewResetting model

            Page.Confirmed ->
                viewConfirmed model.language configuration


viewResetting : Page.Main -> List (Html Page.LogicMsg)
viewResetting main =
    let
        isValidPassword =
            PasswordInput.isValidPassword main.passwordInput

        enterAction =
            MaybeUtil.optional isValidPassword <| onEnter Page.Confirm

        password1 =
            "password1"

        password2 =
            "password2"
    in
    [ h1 [] [ text <| .accountRecovery <| .language <| main ]
    , form []
        [ label [ for password1 ] [ text <| .newPassword <| .language <| main ]
        , input
            ([ MaybeUtil.defined <|
                onInput <|
                    flip PasswordInput.lenses.password1.set
                        main.passwordInput
                        >> Page.SetPasswordInput
             , MaybeUtil.defined <| type_ "password"
             , MaybeUtil.defined <| value <| PasswordInput.lenses.password1.get <| main.passwordInput
             , MaybeUtil.defined <| Style.classes.editable
             , enterAction
             , MaybeUtil.defined <| id <| password1
             ]
                |> Maybe.Extra.values
            )
            []
        , label [ for password2 ] [ text <| .newPasswordRepetition <| .language <| main ]
        , input
            ([ MaybeUtil.defined <|
                onInput <|
                    flip PasswordInput.lenses.password2.set
                        main.passwordInput
                        >> Page.SetPasswordInput
             , MaybeUtil.defined <| type_ "password"
             , MaybeUtil.defined <| value <| PasswordInput.lenses.password2.get <| main.passwordInput
             , MaybeUtil.defined <| Style.classes.editable
             , enterAction
             , MaybeUtil.defined <| id <| password2
             ]
                |> Maybe.Extra.values
            )
            []
        , button
            [ onClick Page.Confirm
            , Style.classes.button.confirm
            , disabled <| not <| isValidPassword
            , type_ "button"
            ]
            [ text <| .updatePassword <| .language <| main ]
        ]
    ]


viewConfirmed : Page.Language -> Configuration -> List (Html Page.LogicMsg)
viewConfirmed language configuration =
    [ text <| .successfullyUpdatedPassword <| language
    , Links.toLoginButton
        { configuration = configuration
        , buttonText = language |> .mainPage
        }
    ]
