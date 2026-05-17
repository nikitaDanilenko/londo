module Pages.LoginTest.LoginTest exposing (..)

import Addresses.Frontend
import Fuzz exposing (string)
import Html
import Html.Attributes
import Language.Language as Language
import Pages.Login.Page
import Pages.Login.View
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Test exposing (..)
import Test.Html.Event as Event
import Test.Html.Query as Query
import Test.Html.Selector exposing (attribute, tag)
import Types.User.Login


credentials : Types.User.Login.ClientInput
credentials =
    Types.User.Login.initial


mainModel : Pages.Login.Page.Main
mainModel =
    { credentials = credentials
    , language = Language.default.login
    }


view : Html.Html Pages.Login.Page.LogicMsg
view =
    Html.div [] (Pages.Login.View.viewMain mainModel)


inputs : Test
inputs =
    describe "login"
        [ fuzz string "set nickname" <|
            \nickname ->
                view
                    |> Query.fromHtml
                    |> Query.find [ tag "input", attribute (Html.Attributes.type_ "text") ]
                    |> Event.simulate (Event.input nickname)
                    |> Event.expect
                        (Pages.Login.Page.SetCredentials
                            (Types.User.Login.lenses.nickname.set nickname credentials)
                        )
        , fuzz string "set password" <|
            \password ->
                view
                    |> Query.fromHtml
                    |> Query.find [ tag "input", attribute (Html.Attributes.type_ "password") ]
                    |> Event.simulate (Event.input password)
                    |> Event.expect
                        (Pages.Login.Page.SetCredentials
                            (Types.User.Login.lenses.password.set password credentials)
                        )
        ]


onLoginSubmit : Test
onLoginSubmit =
    test "Submitting login form triggers Login" <|
        \_ ->
            view
                |> Query.fromHtml
                |> Query.find [ tag "form" ]
                |> Event.simulate Event.submit
                |> Event.expect Pages.Login.Page.Login


registrationLink : Test
registrationLink =
    test "Registration link is correct" <|
        \_ ->
            view
                |> Query.fromHtml
                |> Query.findAll [ attribute Style.classes.button.navigation ]
                |> Query.index 0
                |> Query.has
                    [ attribute <|
                        Html.Attributes.href <|
                            Links.frontendPage <|
                                Addresses.Frontend.requestRegistration.address ()
                    ]


recoveryLink : Test
recoveryLink =
    test "Recovery link is correct" <|
        \_ ->
            view
                |> Query.fromHtml
                |> Query.findAll [ attribute Style.classes.button.navigation ]
                |> Query.index 1
                |> Query.has
                    [ attribute <|
                        Html.Attributes.href <|
                            Links.frontendPage <|
                                Addresses.Frontend.requestRecovery.address ()
                    ]