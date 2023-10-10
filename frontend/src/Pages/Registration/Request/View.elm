module Pages.Registration.Request.View exposing (view)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Html, button, form, h1, input, label, main_, text)
import Html.Attributes exposing (disabled, for, type_)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Language.Language as Language
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Pages.Registration.Request.Page as Page
import Pages.Util.Links as Links
import Pages.Util.Style as Style
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
    main_ [ Style.ids.requestRegistration ] <|
        case main.mode of
            Page.Editing ->
                viewEditing main

            Page.Confirmed ->
                viewConfirmed configuration main.language


viewEditing : Page.Main -> List (Html Page.LogicMsg)
viewEditing main =
    let
        isValid =
            ValidatedInput.isValid main.nickname && ValidatedInput.isValid main.email

        enterAction =
            MaybeUtil.optional isValid <| onEnter Page.Request

        username =
            "username"

        email =
            "email"
    in
    [ h1 [] [ text <| main.language.header ]
    , form
        []
        [ label [ for username ] [ text <| main.language.nickname ]
        , input
            ([ MaybeUtil.defined <|
                onInput <|
                    flip (ValidatedInput.lift LensUtil.identityLens).set main.nickname
                        >> Page.SetNickname
             , MaybeUtil.defined <| Style.classes.editable
             , enterAction
             , MaybeUtil.defined <| type_ "text"
             ]
                |> Maybe.Extra.values
            )
            []
        , label [ for email ] [ text <| main.language.email ]
        , input
            ([ MaybeUtil.defined <|
                onInput <|
                    flip (ValidatedInput.lift LensUtil.identityLens).set main.email
                        >> Page.SetEmail
             , MaybeUtil.defined <| Style.classes.editable
             , enterAction
             , MaybeUtil.defined <| type_ "email"
             ]
                |> Maybe.Extra.values
            )
            []
        , button
            [ onClick Page.Request
            , Style.classes.button.confirm
            , disabled <| not <| isValid
            ]
            [ text <| main.language.register ]
        ]
    ]


viewConfirmed : Configuration -> Language.RequestRegistration -> List (Html Page.LogicMsg)
viewConfirmed configuration language =
    [ text <| language.registrationSuccessful
    , Links.toLoginButton
        { configuration = configuration
        , buttonText = language.mainPage
        }
    ]
