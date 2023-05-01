module Pages.Util.HtmlUtil exposing (Column, RowWithControls, menuIcon, onEscape, searchAreaWith, toggleControlsCell, withExtraAttributes)

import Html exposing (Attribute, Html, button, div, input, label, td, text)
import Html.Attributes exposing (disabled, value)
import Html.Events exposing (on, onClick, onInput)
import Keyboard.Event exposing (KeyboardEvent)
import Keyboard.Key as Key
import Material.Icons
import Material.Icons.Types
import Maybe.Extra
import Pages.Util.Links as Links
import Pages.Util.Style as Style


searchAreaWith :
    { msg : String -> msg
    , searchString : String
    }
    -> Html msg
searchAreaWith ps =
    div [ Style.classes.search.area ]
        [ label [] [ text Links.lookingGlass ]
        , input
            [ onInput <| ps.msg
            , value <| ps.searchString
            , Style.classes.search.field
            ]
            []
        , button
            [ Style.classes.button.cancel
            , onClick <| ps.msg ""
            , disabled <| String.isEmpty <| ps.searchString
            ]
            [ text "Clear" ]
        ]


onEscape : msg -> Attribute msg
onEscape =
    mkEscapeEventMsg
        >> Keyboard.Event.considerKeyboardEvent
        >> on "keydown"


mkEscapeEventMsg : msg -> KeyboardEvent -> Maybe msg
mkEscapeEventMsg msg keyboardEvent =
    Just msg |> Maybe.Extra.filter (always (keyboardEvent.keyCode == Key.Escape))


menuIcon : Html msg
menuIcon =
    Material.Icons.list 20 Material.Icons.Types.Inherit


{-| Todo: This is a little awkward:
The cell has the onClick command, but the button does not.
If the button also has the command, the toggle fires twice, and there is no change.
If only the button has the command, there is a tiny space around the button, which does not trigger the toggle.
There is likely a better solution than this workaround.
-}
toggleControlsCell : msg -> Html msg
toggleControlsCell msg =
    td [ Style.classes.toggle, msg |> onClick ]
        [ button [ Style.classes.button.menu ] [ menuIcon ] ]


type alias Column msg =
    { attributes : List (Attribute msg)
    , children : List (Html msg)
    }


withExtraAttributes : List (Attribute msg) -> Column msg -> Html msg
withExtraAttributes extra column =
    td (column.attributes ++ extra) column.children


type alias RowWithControls msg =
    { display : List (Column msg)
    , controls : List (Html msg)
    }
