module Pages.Util.Choice.Pagination exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Util.PaginationSettings as PaginationSettings exposing (PaginationSettings)


type alias Pagination =
    { elements : PaginationSettings
    , choices : PaginationSettings
    }


initial : Pagination
initial =
    { elements = PaginationSettings.initial
    , choices = PaginationSettings.initial
    }


lenses :
    { elements : Lens Pagination PaginationSettings
    , choices : Lens Pagination PaginationSettings
    }
lenses =
    { elements = Lens .elements (\b a -> { a | elements = b })
    , choices = Lens .choices (\b a -> { a | choices = b })
    }
