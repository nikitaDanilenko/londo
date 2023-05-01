module Util.EditState exposing (EditState(..), lenses, unpack)

import Monocle.Optional exposing (Optional)


type EditState update
    = View Bool
    | Update update
    | Delete


lenses :
    { toggle : Optional (EditState update) Bool
    , update : Optional (EditState update) update
    }
lenses =
    { toggle = Optional toToggle setToggle
    , update = Optional toUpdate setUpdate
    }


unpack :
    { onView : Bool -> a
    , onUpdate : update -> a
    , onDelete : a
    }
    -> EditState update
    -> a
unpack fs editState =
    case editState of
        View showControls ->
            fs.onView showControls

        Update update ->
            fs.onUpdate update

        Delete ->
            fs.onDelete


toUpdate : EditState update -> Maybe update
toUpdate editState =
    case editState of
        Update update ->
            Just update

        _ ->
            Nothing


setUpdate : update -> EditState update -> EditState update
setUpdate update editState =
    case editState of
        Update _ ->
            Update update

        e ->
            e


toToggle : EditState update -> Maybe Bool
toToggle editState =
    case editState of
        View toggle ->
            Just toggle

        _ ->
            Nothing


setToggle : Bool -> EditState update -> EditState update
setToggle value editState =
    case editState of
        View _ ->
            View value

        e ->
            e
