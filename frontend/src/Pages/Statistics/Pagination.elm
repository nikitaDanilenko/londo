module Pages.Statistics.Pagination exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Util.PaginationSettings as PaginationSettings exposing (PaginationSettings)
import Types.Project.Id
import Util.DictList as DictList exposing (DictList)


type alias Pagination =
    { projects : PaginationSettings
    , unfinishedTasks : DictList Types.Project.Id.Id PaginationSettings
    , finishedTasks : DictList Types.Project.Id.Id PaginationSettings
    }


initial : Pagination
initial =
    { projects = PaginationSettings.initial
    , unfinishedTasks = DictList.empty
    , finishedTasks = DictList.empty
    }


lenses :
    { projects : Lens Pagination PaginationSettings
    , unfinishedTasks : Lens Pagination (DictList Types.Project.Id.Id PaginationSettings)
    , finishedTasks : Lens Pagination (DictList Types.Project.Id.Id PaginationSettings)
    }
lenses =
    { projects = Lens .projects (\b a -> { a | projects = b })
    , unfinishedTasks = Lens .unfinishedTasks (\b a -> { a | unfinishedTasks = b })
    , finishedTasks = Lens .finishedTasks (\b a -> { a | finishedTasks = b })
    }
