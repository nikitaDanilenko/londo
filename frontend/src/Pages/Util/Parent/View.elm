module Pages.Util.Parent.View exposing (..)

import Html exposing (Attribute, Html, table, tbody)
import Pages.Util.Parent.Page as Page
import Pages.Util.Style as Style
import Util.Editing as Editing


viewMain :
    { tableHeader : Html msg
    , onView : parent -> Bool -> List (Html msg)
    , onUpdate : parent -> update -> List (Html msg)
    , onDelete : parent -> List (Html msg)
    }
    -> Page.Main parent update
    -> Html msg
viewMain ps main =
    table [ Style.classes.elementsWithControlsTable ]
        (ps.tableHeader
            :: [ tbody []
                    (Editing.unpack
                        { onView = ps.onView
                        , onUpdate = ps.onUpdate
                        , onDelete = ps.onDelete
                        }
                        main.parent
                    )
               ]
        )
