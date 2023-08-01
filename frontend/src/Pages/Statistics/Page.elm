module Pages.Statistics.Page exposing (..)

import Language.Language as Language
import Monocle.Lens exposing (Lens)
import Pages.Statistics.EditingResolvedProject exposing (EditingResolvedProject)
import Pages.Statistics.Pagination as Pagination exposing (Pagination)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Types.Dashboard.Dashboard
import Types.Dashboard.DeeplyResolved
import Types.Dashboard.Id
import Types.Project.Id
import Types.Project.Project
import Types.Project.Resolved
import Types.Task.Id
import Types.Task.Task
import Types.Task.Update
import Util.DictList as DictList exposing (DictList)
import Util.Editing as Editing exposing (Editing)
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main Initial


type alias DashboardId =
    Types.Dashboard.Id.Id


type alias Dashboard =
    Types.Dashboard.Dashboard.Dashboard


type alias DeeplyResolvedDashboard =
    Types.Dashboard.DeeplyResolved.DeeplyResolved


type alias ProjectId =
    Types.Project.Id.Id


type alias Project =
    Types.Project.Project.Project


type alias ResolvedProject =
    Types.Project.Resolved.Resolved


type alias Task =
    Types.Task.Task.Task


type alias TaskUpdate =
    Types.Task.Update.ClientInput


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


type alias Languages =
    { taskEditor : TaskEditorLanguage
    , project : ProjectLanguage
    , dashboard : DashboardLanguage
    , statistics : StatisticsLanguage
    }


type alias Main =
    { jwt : JWT
    , dashboard : Dashboard
    , projects : DictList ProjectId EditingResolvedProject
    , searchString : String
    , pagination : Pagination
    , languages : Languages
    }


type alias Initial =
    { jwt : JWT
    , deeplyResolvedDashboard : Maybe DeeplyResolvedDashboard
    , languages : Languages
    }


initial : Languages -> AuthorizedAccess -> Model
initial languages authorizedAccess =
    { jwt = authorizedAccess.jwt
    , deeplyResolvedDashboard = Nothing
    , languages = languages
    }
        |> Tristate.createInitial authorizedAccess.configuration


initialToMain : Initial -> Maybe Main
initialToMain i =
    Maybe.map
        (\deeplyResolvedDashboard ->
            { jwt = i.jwt
            , dashboard = deeplyResolvedDashboard.dashboard
            , projects =
                deeplyResolvedDashboard.resolvedProjects
                    |> List.map
                        (\resolvedProject ->
                            { project = resolvedProject.project
                            , tasks =
                                resolvedProject
                                    |> .tasks
                                    |> List.map Editing.asView
                                    |> DictList.fromListWithKey (.original >> .id)
                            }
                        )
                    |> DictList.fromListWithKey (.project >> .id)
            , searchString = ""
            , pagination = Pagination.initial
            , languages = i.languages
            }
        )
        i.deeplyResolvedDashboard


lenses :
    { initial :
        { deeplyResolvedDashboard : Lens Initial (Maybe DeeplyResolvedDashboard)
        }
    , main :
        { dashboard : Lens Main Dashboard
        , projects : Lens Main (DictList ProjectId EditingResolvedProject)
        , searchString : Lens Main String
        , pagination : Lens Main Pagination
        }
    }
lenses =
    { initial =
        { deeplyResolvedDashboard = Lens .deeplyResolvedDashboard (\b a -> { a | deeplyResolvedDashboard = b })
        }
    , main =
        { dashboard = Lens .dashboard (\b a -> { a | dashboard = b })
        , projects = Lens .projects (\b a -> { a | projects = b })
        , searchString = Lens .searchString (\b a -> { a | searchString = b })
        , pagination = Lens .pagination (\b a -> { a | pagination = b })
        }
    }


type alias Flags =
    { dashboardId : DashboardId
    , authorizedAccess : AuthorizedAccess
    }


type LogicMsg
    = GotFetchDeeplyDashboardResponse (HttpUtil.GraphQLResult DeeplyResolvedDashboard)
    | EditTask ProjectId TaskId TaskUpdate
    | SaveEditTask ProjectId TaskId
    | GotSaveEditTaskResponse ( ProjectId, HttpUtil.GraphQLResult Task )
    | ToggleControls ProjectId TaskId
    | EnterEditTask ProjectId TaskId
    | ExitEditTask ProjectId TaskId
    | SetPagination Pagination
    | SetSearchString String


type alias Msg =
    Tristate.Msg LogicMsg
