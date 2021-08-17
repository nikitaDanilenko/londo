module Pages.Util.Links exposing (..)

import Bootstrap.Button
import Html exposing (Attribute, Html)
import Html.Attributes exposing (href)


linkButtonWithDisabled :
    { url : String
    , attributes : List (Attribute msg)
    , children : List (Html msg)
    , isDisabled : Bool
    }
    -> Html msg
linkButtonWithDisabled params =
    Bootstrap.Button.linkButton
        [ Bootstrap.Button.disabled params.isDisabled
        , Bootstrap.Button.attrs (href params.url :: params.attributes)
        ]
        params.children