module Pages.Statistics.Page exposing (..)

import Language.Language as Language
import Math.Positive
import Monocle.Lens exposing (Lens)
import Pages.Statistics.EditingResolvedProject exposing (EditingResolvedProject)
import Pages.Statistics.Pagination as Pagination exposing (Pagination)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.PaginationSettings exposing (PaginationSettings)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Types.Dashboard.Analysis
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Types.Dashboard.Statistics
import Types.Project.Id
import Types.Project.Project
import Types.Project.Resolved
import Types.Task.Analysis
import Types.Task.Id
import Types.Task.Task
import Types.Task.TaskWithSimulation
import Util.DictList as DictList exposing (DictList)
import Util.Editing as Editing exposing (Editing)
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


type alias ResolvedProject =
    Types.Project.Resolved.Resolved


type alias Task =
    Types.Task.Task.Task


type alias TaskAnalysis =
    Types.Task.Analysis.Analysis


type alias TaskUpdate =
    Types.Task.TaskWithSimulation.ClientInput


type alias TaskId =
    Types.Task.Id.Id


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
    { jwt : JWT
    , dashboard : Dashboard
    , dashboardStatistics : DashboardStatistics
    , projects : DictList ProjectId EditingResolvedProject
    , viewType : ViewType
    , searchString : String
    , pagination : Pagination
    , languages : Languages
    }


type alias Initial =
    { jwt : JWT
    , dashboardAnalysis : Maybe DashboardAnalysis
    , languages : Languages
    }


initial : Languages -> AuthorizedAccess -> Model
initial languages authorizedAccess =
    { jwt = authorizedAccess.jwt
    , dashboardAnalysis = Nothing
    , languages = languages
    }
        |> Tristate.createInitial authorizedAccess.configuration


initialToMain : Initial -> Maybe Main
initialToMain i =
    Maybe.map
        (\dashboardAnalysis ->
            { jwt = i.jwt
            , dashboard = dashboardAnalysis.dashboard
            , dashboardStatistics = dashboardAnalysis.dashboardStatistics
            , projects =
                dashboardAnalysis.projectAnalyses
                    |> List.map
                        (\resolvedProject ->
                            { project = resolvedProject.project
                            , tasks =
                                resolvedProject
                                    |> .tasks
                                    |> List.map Editing.asView
                                    |> DictList.fromListWithKey Types.Task.Id.ordering (.original >> .task >> .id)
                            }
                        )
                    |> DictList.fromListWithKey Types.Project.Id.ordering (.project >> .id)
            , viewType = Counting
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
        , projects : Lens Main (DictList ProjectId EditingResolvedProject)
        , viewType : Lens Main ViewType
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
    , authorizedAccess : AuthorizedAccess
    }


type LogicMsg
    = GotFetchDashboardAnalysisResponse (HttpUtil.GraphQLResult DashboardAnalysis)
    | EditTask ProjectId TaskId TaskUpdate
    | SaveEditTask ProjectId TaskId
    | GotSaveEditTaskResponse ProjectId (HttpUtil.GraphQLResult TaskAnalysis)
    | GotFetchUpdatedStatisticsResponse ProjectId TaskAnalysis (HttpUtil.GraphQLResult DashboardStatistics)
    | ToggleControls ProjectId TaskId
    | EnterEditTask ProjectId TaskId
    | ExitEditTask ProjectId TaskId
    | SetProjectsPagination PaginationSettings
    | SetSearchString String
    | SetViewType ViewType


type ViewType
    = Total
    | Counting


type alias Msg =
    Tristate.Msg LogicMsg



-- todo: The number of decimal places should be a user setting that is fetched (regularly?).


numberOfDecimalPlaces : Math.Positive.Positive
numberOfDecimalPlaces =
    Math.Positive.fromInt 6 |> Maybe.withDefault Math.Positive.one
