module Pages.Util.PaginationSettings exposing (..)

import Monocle.Compose as Compose
import Monocle.Lens exposing (Lens)


type alias PaginationSettings =
    { currentPage : Int
    , itemsPerPage : Int
    }


initial : PaginationSettings
initial =
    { currentPage = 1
    , itemsPerPage = 25
    }


lenses :
    { currentPage : Lens PaginationSettings Int
    , itemsPerPage : Lens PaginationSettings Int
    }
lenses =
    { currentPage = Lens .currentPage (\b a -> { a | currentPage = b })
    , itemsPerPage = Lens .itemsPerPage (\b a -> { a | itemsPerPage = b })
    }


updateCurrentPage :
    { a
        | pagination : Lens model pagination
        , items : Lens pagination PaginationSettings
    }
    -> model
    -> Int
    -> pagination
updateCurrentPage ps model currentPage =
    ps.pagination.get model
        |> (ps.items
                |> Compose.lensWithLens lenses.currentPage
           ).set
            currentPage


setSearchStringAndReset :
    { searchStringLens : Lens model String
    , paginationSettingsLens : Lens model PaginationSettings
    }
    -> String
    -> model
    -> model
setSearchStringAndReset ls searchString =
    ls.searchStringLens.set searchString
        >> (ls.paginationSettingsLens
                |> Compose.lensWithLens lenses.currentPage
           ).set
            1
