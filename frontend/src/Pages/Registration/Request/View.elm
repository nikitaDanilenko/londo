module Pages.Registration.Request.View exposing (view)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Html, button, div, input, label, table, tbody, td, text, tr)
import Html.Attributes exposing (disabled)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Language.Language as Language
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Pages.Registration.Request.Page as Page
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Util.LensUtil as LensUtil
import Util.MaybeUtil as MaybeUtil
import Util.ValidatedInput as ValidatedInput


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
                [ viewEditing main ]

            Page.Confirmed ->
                [ viewConfirmed configuration main.language ]


viewEditing : Page.Main -> Html Page.LogicMsg
viewEditing main =
    let
        isValid =
            ValidatedInput.isValid main.nickname && ValidatedInput.isValid main.email

        enterAction =
            MaybeUtil.optional isValid <| onEnter Page.Request
    in
    div [ Style.ids.requestRegistration ]
        [ div [] [ label [ Style.classes.info ] [ text <| main.language.header ] ]
        , table []
            [ tbody []
                [ tr []
                    [ td [] [ label [] [ text <| main.language.nickname ] ]
                    , td []
                        [ input
                            ([ MaybeUtil.defined <|
                                onInput <|
                                    flip (ValidatedInput.lift LensUtil.identityLens).set main.nickname
                                        >> Page.SetNickname
                             , MaybeUtil.defined <| Style.classes.editable
                             , enterAction
                             ]
                                |> Maybe.Extra.values
                            )
                            []
                        ]
                    ]
                , tr []
                    [ td [] [ label [] [ text <| main.language.email ] ]
                    , td []
                        [ input
                            ([ MaybeUtil.defined <|
                                onInput <|
                                    flip (ValidatedInput.lift LensUtil.identityLens).set main.email
                                        >> Page.SetEmail
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
        , div []
            [ button
                [ onClick Page.Request
                , Style.classes.button.confirm
                , disabled <| not <| isValid
                ]
                [ text <| main.language.register ]
            ]
        ]


viewConfirmed : Configuration -> Language.RequestRegistration -> Html Page.LogicMsg
viewConfirmed configuration language =
    div [ Style.ids.registrationRequestSent ]
        [ div [] [ label [] [ text <| language.registrationSuccessful ] ]
        , div []
            [ Links.toLoginButton
                { configuration = configuration
                , buttonText = language.mainPage
                }
            ]
        ]
