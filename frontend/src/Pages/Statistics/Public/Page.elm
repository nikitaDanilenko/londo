module Pages.Statistics.Public.Page exposing (..)

-- todo: Is Pages.Statistics.Pagination the correct Pagination?

import Configuration exposing (Configuration)
import Language.Language as Language
import Monocle.Lens exposing (Lens)
import Pages.Statistics.Page
import Pages.Statistics.Pagination as Pagination exposing (Pagination)
import Pages.Util.PaginationSettings exposing (PaginationSettings)
import Pages.View.Tristate as Tristate
import Types.Dashboard.Analysis
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Types.Dashboard.Statistics
import Types.Project.Id
import Types.Project.Project
import Types.Task.Analysis
import Util.DictList as DictList exposing (DictList)
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main Initial


type alias DashboardId =
    Types.Dashboard.Id.Id


type alias Dashboard =
    Types.Dashboard.Dashboard.Dashboard


type alias DashboardAnalysis =
    Types.Dashboard.Analysis.Analysis


type alias ProjectId =
    Types.Project.Id.Id


type alias Project =
    Types.Project.Project.Project


type alias ProjectAnalysis =
    { project : Project
    , tasks : List TaskAnalysis
    }


type alias TaskAnalysis =
    Types.Task.Analysis.Analysis


type alias TaskEditorLanguage =
    Language.TaskEditor


type alias ProjectLanguage =
    Language.ProjectEditor


type alias DashboardLanguage =
    Language.DashboardEditor


type alias StatisticsLanguage =
    Language.Statistics


type alias DashboardStatistics =
    Types.Dashboard.Statistics.Statistics


type alias Languages =
    { taskEditor : TaskEditorLanguage
    , project : ProjectLanguage
    , dashboard : DashboardLanguage
    , statistics : StatisticsLanguage
    }


type alias Main =
    { dashboard : Dashboard
    , dashboardStatistics : DashboardStatistics
    , projects : DictList ProjectId ProjectAnalysis
    , viewType : Pages.Statistics.Page.ViewType
    , searchString : String
    , pagination : Pagination
    , languages : Languages
    }


type alias Initial =
    { dashboardAnalysis : Maybe DashboardAnalysis
    , languages : Languages
    }


initial : Languages -> Language.ErrorHandling -> Configuration -> Model
initial languages errorLanguage configuration =
    { dashboardAnalysis = Nothing
    , languages = languages
    }
        |> Tristate.createInitial configuration errorLanguage


initialToMain : Initial -> Maybe Main
initialToMain i =
    Maybe.map
        (\dashboardAnalysis ->
            { dashboard = dashboardAnalysis.dashboard
            , dashboardStatistics = dashboardAnalysis.dashboardStatistics
            , projects =
                dashboardAnalysis.projectAnalyses
                    |> DictList.fromListWithKey Types.Project.Id.ordering (.project >> .id)
            , viewType = Pages.Statistics.Page.Counting
            , searchString = ""
            , pagination = Pagination.initial
            , languages = i.languages
            }
        )
        i.dashboardAnalysis


lenses :
    { initial :
        { dashboardAnalysis : Lens Initial (Maybe DashboardAnalysis)
        }
    , main :
        { dashboard : Lens Main Dashboard
        , dashboardStatistics : Lens Main DashboardStatistics
        , projects : Lens Main (DictList ProjectId ProjectAnalysis)
        , viewType : Lens Main Pages.Statistics.Page.ViewType
        , searchString : Lens Main String
        , pagination : Lens Main Pagination
        }
    }
lenses =
    { initial =
        { dashboardAnalysis = Lens .dashboardAnalysis (\b a -> { a | dashboardAnalysis = b })
        }
    , main =
        { dashboard = Lens .dashboard (\b a -> { a | dashboard = b })
        , dashboardStatistics = Lens .dashboardStatistics (\b a -> { a | dashboardStatistics = b })
        , projects = Lens .projects (\b a -> { a | projects = b })
        , viewType = Lens .viewType (\b a -> { a | viewType = b })
        , searchString = Lens .searchString (\b a -> { a | searchString = b })
        , pagination = Lens .pagination (\b a -> { a | pagination = b })
        }
    }


type alias Flags =
    { dashboardId : DashboardId
    , configuration : Configuration
    }


type LogicMsg
    = GotFetchDashboardAnalysisResponse (HttpUtil.GraphQLResult DashboardAnalysis)
    | SetProjectsPagination PaginationSettings
    | SetSearchString String
    | SetViewType Pages.Statistics.Page.ViewType


type alias Msg =
    Tristate.Msg LogicMsg
