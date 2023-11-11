module Pages.Util.HtmlUtil exposing (Column, RowWithControls, Structure, menuIcon, onEscape, searchAreaWith, toggleControlsCell, withAttributes, withExtraAttributes)

import Html exposing (Attribute, Html, button, form, input, label, node, td, text)
import Html.Attributes exposing (disabled, for, id, placeholder, type_, value)
import Html.Events exposing (on, onClick, onInput, onSubmit)
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
    , clearWord : String
    }
    -> Html msg
searchAreaWith ps =
    let
        searchField =
            "search-field"
    in
    node "search"
        []
        [ form
            -- Todo: This a somewhat hacky: The submission of the given string is a workaround for reloading,
            --       but this triggers a message, while deactivating the messaging altogether makes more sense.
            [ onSubmit <| ps.msg <| ps.searchString ]
            [ label
                [ for searchField ]
                [ text Links.lookingGlass ]
            , input
                [ type_ "search"
                , Style.ids.searchField
                , onInput <| ps.msg
                , value <| ps.searchString
                , placeholder "Text"
                , id searchField
                ]
                []
            , button
                [ Style.classes.button.clear
                , type_ "reset"
                , onClick <| ps.msg ""
                , disabled <| String.isEmpty <| ps.searchString
                ]
                [ text <| .clearWord <| ps ]
            ]
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


type alias Structure msg =
    { constructor : List (Attribute msg) -> List (Html msg) -> Html msg
    , attributes : List (Attribute msg)
    , children : List (Html msg)
    }


withAttributes : List (Attribute msg) -> Structure msg -> Html msg
withAttributes extra structure =
    structure.constructor (extra ++ structure.attributes) structure.children


type alias RowWithControls msg =
    { display : List (Column msg)
    , controls : List (Html msg)
    }
