module Pages.Settings.View exposing (..)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Html, button, form, h1, h2, input, label, section, text)
import Html.Attributes exposing (disabled, for, id, type_, value)
import Html.Events exposing (onClick, onInput, onSubmit)
import Html.Events.Extra exposing (onEnter)
import LondoGQL.Enum.LogoutMode
import Maybe.Extra
import Monocle.Compose as Compose
import Pages.Settings.Page as Page
import Pages.Util.ComplementInput as ComplementInput
import Pages.Util.PasswordInput as PasswordInput
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil exposing (Page(..))
import Pages.View.Tristate as Tristate
import Util.MaybeUtil as MaybeUtil


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
        , currentPage = Just UserSettings
        , showNavigation = True
        , id = Style.ids.settings
        }
    <|
        case main.mode of
            Page.Regular ->
                viewRegular main.language main

            Page.RequestedDeletion ->
                viewRequestedDeletion main.language


viewRegular : Page.Language -> Page.Main -> List (Html Page.LogicMsg)
viewRegular language main =
    let
        isValidPassword =
            PasswordInput.isValidPassword main.complementInput.passwordInput

        enterPasswordAction =
            MaybeUtil.optional isValidPassword <| onEnter Page.UpdatePassword

        password1Lens =
            ComplementInput.lenses.passwordInput
                |> Compose.lensWithLens PasswordInput.lenses.password1

        password2Lens =
            ComplementInput.lenses.passwordInput
                |> Compose.lensWithLens PasswordInput.lenses.password2

        newDisplayName =
            "new-display-name"

        password1 =
            "password1"

        password2 =
            "password2"
    in
    [ h1 [] [ text <| .userSettings <| language ]
    , section []
        [ form []
            [ label [] [ text <| .nickname <| language ]
            , input [ type_ "text", disabled True, value <| .nickname <| main.user ] []
            , label [] [ text <| .email <| language ]
            , input [ type_ "text", disabled True, value <| .email <| main.user ] []
            , label [] [ text <| .displayName <| language ]
            , input [ type_ "text", disabled True, value <| Maybe.withDefault "" <| .displayName <| main.user ] []
            ]
        ]
    , section []
        [ h2 []
            [ text <| .changeSettings <| language ]
        , form
            [ onSubmit <| Page.UpdateSettings ]
            [ label
                [ for newDisplayName ]
                [ text <| .newDisplayName <| language ]
            , input
                [ type_ "text"
                , id newDisplayName
                , onInput
                    (Just
                        >> Maybe.Extra.filter (String.isEmpty >> not)
                        >> (flip ComplementInput.lenses.displayName.set
                                main.complementInput
                                >> Page.SetComplementInput
                           )
                    )
                , value <| Maybe.withDefault "" <| main.complementInput.displayName
                , Style.classes.editable
                ]
                []
            , button
                [ onClick <| Page.UpdateSettings
                , Style.classes.button.confirm
                , type_ "submit"
                ]
                [ text <| .updateSettings <| language ]
            ]
        ]
    , section []
        [ h2 [] [ text <| .changePassword <| language ]
        , form
            [ onSubmit <| Page.UpdatePassword ]
            [ label
                [ for password1 ]
                [ text <| .newPassword <| language ]
            , input
                ([ MaybeUtil.defined <|
                    onInput <|
                        flip password1Lens.set
                            main.complementInput
                            >> Page.SetComplementInput
                 , MaybeUtil.defined <| type_ "password"
                 , MaybeUtil.defined <| value <| password1Lens.get <| main.complementInput
                 , MaybeUtil.defined <| Style.classes.editable
                 , enterPasswordAction
                 , MaybeUtil.defined <| id password1
                 ]
                    |> Maybe.Extra.values
                )
                []
            , label
                [ for password2 ]
                [ text <| .newPasswordRepetition <| language ]
            , input
                ([ MaybeUtil.defined <|
                    onInput <|
                        flip password2Lens.set
                            main.complementInput
                            >> Page.SetComplementInput
                 , MaybeUtil.defined <| type_ "password"
                 , MaybeUtil.defined <| value <| password2Lens.get <| main.complementInput
                 , MaybeUtil.defined <| Style.classes.editable
                 , enterPasswordAction
                 , MaybeUtil.defined <| id password2
                 ]
                    |> Maybe.Extra.values
                )
                []
            , button
                [ onClick <| Page.UpdatePassword
                , Style.classes.button.confirm
                , disabled <| not <| isValidPassword
                , type_ "submit"
                ]
                [ text <| .updatePassword <| language ]
            ]
        ]
    , section []
        [ h2 [] [ text <| .dangerZone <| language ]
        , button
            [ onClick <| Page.RequestDeletion
            , Style.classes.button.delete
            ]
            [ text <| .deleteAccount <| language ]
        , button
            [ onClick <| Page.Logout LondoGQL.Enum.LogoutMode.ThisSession
            , Style.classes.button.edit
            ]
            [ text <| .logoutThisDevice <| language ]
        , button
            [ onClick <| Page.Logout LondoGQL.Enum.LogoutMode.AllSessions
            , Style.classes.button.edit
            ]
            [ text <| .logoutAllDevices <| language ]
        ]
    ]


viewRequestedDeletion : Page.Language -> List (Html Page.LogicMsg)
viewRequestedDeletion language =
    [ text <| .deletionRequested <| language ]
