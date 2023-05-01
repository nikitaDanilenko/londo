module Pages.Util.ParentEditor.Pagination exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Util.PaginationSettings as PaginationSettings exposing (PaginationSettings)


type alias Pagination =
    { parents : PaginationSettings
    }


initial : Pagination
initial =
    { parents = PaginationSettings.initial
    }


lenses :
    { parents : Lens Pagination PaginationSettings
    }
lenses =
    { parents = Lens .parents (\b a -> { a | parents = b })
    }
