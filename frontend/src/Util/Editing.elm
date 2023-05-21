module Util.Editing exposing (..)

import Monocle.Compose as Compose
import Monocle.Lens as Lens exposing (Lens)
import Monocle.Optional exposing (Optional)
import Util.EditState as EditState exposing (EditState)


type alias Editing original update =
    { original : original
    , editState : EditState update
    }


lenses :
    { original : Lens (Editing original update) original
    , editState : Lens (Editing original update) (EditState update)
    , update : Optional (Editing original update) update
    }
lenses =
    let
        editState =
            Lens .editState (\b a -> { a | editState = b })
    in
    { original = Lens .original (\b a -> { a | original = b })
    , editState = editState
    , update =
        editState
            |> Compose.lensWithOptional EditState.lenses.update
    }


unpack :
    { onView : original -> Bool -> a
    , onUpdate : original -> update -> a
    , onDelete : original -> a
    }
    -> Editing original update
    -> a
unpack fs editing =
    EditState.unpack
        { onView = fs.onView editing.original
        , onUpdate = fs.onUpdate editing.original
        , onDelete = fs.onDelete editing.original
        }
        editing.editState


isUpdate : Editing a b -> Bool
isUpdate =
    unpack
        { onView = \_ _ -> False
        , onUpdate = \_ _ -> True
        , onDelete = \_ -> False
        }


toUpdate : (original -> update) -> Editing original update -> Editing original update
toUpdate to editing =
    lenses.editState.set
        (EditState.Update <| to <| editing.original)
        editing


toDelete : Editing original update -> Editing original update
toDelete =
    lenses.editState.set EditState.Delete


toView : Editing original update -> Editing original update
toView =
    lenses.editState.set (EditState.View False)


extractUpdate : Editing original update -> Maybe update
extractUpdate =
    lenses.editState
        |> Compose.lensWithOptional EditState.lenses.update
        |> .getOption


asView : element -> Editing element update
asView element =
    { original = element
    , editState = EditState.View False
    }


asViewWithElement : element -> Editing element update -> Editing element update
asViewWithElement element =
    lenses.original.set element
        >> Lens.modify lenses.editState
            (EditState.unpack
                { onView = identity
                , onUpdate = always True
                , onDelete = True
                }
                >> EditState.View
            )


toggleControls : Editing element update -> Editing element update
toggleControls editing =
    editing
        |> Lens.modify lenses.editState
            (EditState.unpack
                { onView = not >> EditState.View
                , onUpdate = \_ -> EditState.View False
                , onDelete = EditState.View False
                }
            )
