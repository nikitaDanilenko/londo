module Pages.Registration.Confirm.View exposing (view)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Html, button, h1, input, table, tbody, td, text, tr)
import Html.Attributes exposing (disabled, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Language.Language as Language
import Maybe.Extra
import Monocle.Compose as Compose
import Pages.Registration.Confirm.Page as Page
import Pages.Util.ComplementInput as ComplementInput
import Pages.Util.Links as Links
import Pages.Util.PasswordInput as PasswordInput
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Util.MaybeUtil as MaybeUtil


view : Page.Model -> Html Page.Msg
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Configuration -> Page.Main -> Html Page.LogicMsg
viewMain configuration main =
    ViewUtil.viewMainWith
        { configuration = configuration
        , currentPage = Nothing
        , showNavigation = False
        }
    <|
        case main.mode of
            Page.Editing ->
                viewEditing main

            Page.Confirmed ->
                viewConfirmed configuration main.language


viewEditing : Page.Main -> List (Html Page.LogicMsg)
viewEditing main =
    let
        isValid =
            PasswordInput.isValidPassword main.complementInput.passwordInput

        enterAction =
            MaybeUtil.optional isValid <| onEnter Page.Request

        password1Lens =
            ComplementInput.lenses.passwordInput
                |> Compose.lensWithLens PasswordInput.lenses.password1

        password2Lens =
            ComplementInput.lenses.passwordInput
                |> Compose.lensWithLens PasswordInput.lenses.password2
    in
    [ h1 [ Style.classes.info ] [ text <| main.language.header ]
    , table []
        [ tbody []
            [ tr []
                [ td [] [ text <| main.language.nickname ]
                , td [] [ text <| main.userIdentifier.nickname ]
                ]
            , tr []
                [ td [] [ text <| main.language.email ]
                , td [] [ text <| main.userIdentifier.email ]
                ]
            , tr []
                [ td [] [ text <| main.language.displayName ]
                , td []
                    [ input
                        ([ MaybeUtil.defined <|
                            onInput <|
                                Just
                                    >> Maybe.Extra.filter (String.isEmpty >> not)
                                    >> (flip ComplementInput.lenses.displayName.set
                                            main.complementInput
                                            >> Page.SetComplementInput
                                       )
                         , MaybeUtil.defined <| Style.classes.editable
                         , enterAction
                         ]
                            |> Maybe.Extra.values
                        )
                        []
                    ]
                ]
            , tr []
                [ td [] [ text <| main.language.password ]
                , td []
                    [ input
                        ([ MaybeUtil.defined <|
                            onInput <|
                                flip password1Lens.set
                                    main.complementInput
                                    >> Page.SetComplementInput
                         , MaybeUtil.defined <| value <| password1Lens.get <| main.complementInput
                         , MaybeUtil.defined <| type_ "password"
                         , MaybeUtil.defined <| Style.classes.editable
                         , enterAction
                         ]
                            |> Maybe.Extra.values
                        )
                        []
                    ]
                ]
            , tr []
                [ td [] [ text <| main.language.passwordRepetition ]
                , td []
                    [ input
                        ([ MaybeUtil.defined <|
                            onInput <|
                                flip password2Lens.set
                                    main.complementInput
                                    >> Page.SetComplementInput
                         , MaybeUtil.defined <| value <| password2Lens.get <| main.complementInput
                         , MaybeUtil.defined <| type_ "password"
                         , MaybeUtil.defined <| Style.classes.editable
                         , enterAction
                         ]
                            |> Maybe.Extra.values
                        )
                        []
                    ]
                ]
            ]
        ]
    , button
        [ onClick Page.Request
        , Style.classes.button.confirm
        , disabled <| not <| isValid
        ]
        [ text <| main.language.confirm ]
    ]


viewConfirmed : Configuration -> Language.ConfirmRegistration -> List (Html Page.LogicMsg)
viewConfirmed configuration language =
    [ text <| language.successfullyCreatedUser
    , Links.toLoginButton
        { configuration = configuration
        , buttonText = language.mainPage
        }
    ]
