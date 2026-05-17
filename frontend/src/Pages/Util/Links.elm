module Pages.Util.Links exposing (..)

import Addresses.Frontend
import Bootstrap.Button
import Browser.Navigation
import Html exposing (Attribute, Html, text)
import Html.Attributes exposing (href)
import Loading
import Pages.Util.Style as Style
import Url.Builder


linkButton :
    { url : String
    , attributes : List (Attribute msg)
    , linkText : String
    }
    -> Html msg
linkButton params =
    Bootstrap.Button.linkButton
        [ Bootstrap.Button.attrs (href params.url :: params.attributes)
        ]
        [ text <| params.linkText ]


special : Int -> String
special =
    Char.fromCode >> String.fromChar


lookingGlass : String
lookingGlass =
    special 128269


loadingSymbol : Html msg
loadingSymbol =
    Loading.render Loading.Spinner Loading.defaultConfig Loading.On


frontendPage : List String -> String
frontendPage pathSteps =
    Url.Builder.absolute pathSteps []


loadFrontendPage : List String -> Cmd msg
loadFrontendPage =
    frontendPage >> Browser.Navigation.load


toLoginButton :
    { buttonText : String
    }
    -> Html msg
toLoginButton params =
    toLoginButtonWith
        { buttonText = params.buttonText
        , attributes = [ Style.classes.button.navigation ]
        }


toLoginButtonWith :
    { buttonText : String
    , attributes : List (Attribute msg)
    }
    -> Html msg
toLoginButtonWith params =
    linkButton
        { url = frontendPage <| Addresses.Frontend.login.address ()
        , attributes = params.attributes
        , linkText = params.buttonText
        }
