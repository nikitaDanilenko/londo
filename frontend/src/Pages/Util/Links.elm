module Pages.Util.Links exposing (..)

import Addresses.Frontend
import Basics.Extra exposing (flip)
import Bootstrap.Button
import Configuration exposing (Configuration)
import Html exposing (Attribute, Html, text)
import Html.Attributes exposing (href)
import Loading
import Pages.Util.Style as Style
import Url.Builder


linkButton :
    { url : String
    , attributes : List (Attribute msg)
    , children : List (Html msg)
    }
    -> Html msg
linkButton params =
    Bootstrap.Button.linkButton
        [ Bootstrap.Button.attrs (href params.url :: params.attributes)
        ]
        params.children


special : Int -> String
special =
    Char.fromCode >> String.fromChar


lookingGlass : String
lookingGlass =
    special 128269


loadingSymbol : Html msg
loadingSymbol =
    Loading.render Loading.Spinner Loading.defaultConfig Loading.On


frontendPage : Configuration -> List String -> String
frontendPage configuration pathSteps =
    [ configuration.mainPageURL, "#" ]
        ++ pathSteps
        |> flip Url.Builder.relative []

toLoginButton :
    { configuration : Configuration
    , buttonText : String
    }
    -> Html msg
toLoginButton params =
    toLoginButtonWith
        { configuration = params.configuration
        , buttonText = params.buttonText
        , attributes = [ Style.classes.button.navigation ]
        }

toLoginButtonWith :
    { configuration : Configuration
    , buttonText : String
    , attributes : List (Attribute msg)
    }
    -> Html msg
toLoginButtonWith params =
    linkButton
        { url = frontendPage params.configuration <| Addresses.Frontend.login.address ()
        , attributes = params.attributes
        , children = [ text <| params.buttonText ]
        }
