module Pages.Statistics.Pagination exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Util.PaginationSettings as PaginationSettings exposing (PaginationSettings)


type alias Pagination =
    { projects : PaginationSettings
    }


initial : Pagination
initial =
    { projects = PaginationSettings.initial
    }


lenses :
    { projects : Lens Pagination PaginationSettings
    }
lenses =
    { projects = Lens .projects (\b a -> { a | projects = b })
    }
