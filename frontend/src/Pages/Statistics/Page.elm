module Pages.Statistics.Page exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Types.Project.Id
import Types.Project.Project
import Types.Task.Id
import Types.Task.Task
import Types.Task.Update
import Util.DictList exposing (DictList)
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main Initial


type alias DashboardId =
    Types.Dashboard.Id.Id


type alias Dashboard =
    Types.Dashboard.Dashboard.Dashboard


type alias ProjectId =
    Types.Project.Id.Id


type alias Project =
    Types.Project.Project.Project


type alias Task =
    Types.Task.Task.Task


type alias TaskUpdate =
    Types.Task.Update.ClientInput


type alias TaskId =
    Types.Task.Id.Id


type alias Main =
    { jwt : JWT
    , dashboards : List Dashboard
    , projectsByDashboard : DictList DashboardId (List Project)
    , tasksByProjectId : DictList ProjectId (List Task)
    , search : String
    }


type alias Initial =
    { jwt : JWT
    , dashboards : Maybe (List Dashboard)
    , projectsByDashboard : Maybe (DictList DashboardId (List Project))
    , tasksByProjectId : Maybe (DictList ProjectId (List Task))
    }


initial : AuthorizedAccess -> Model
initial authorizedAccess =
    { jwt = authorizedAccess.jwt
    , dashboards = Nothing
    , projectsByDashboard = Nothing
    , tasksByProjectId = Nothing
    }
        |> Tristate.createInitial authorizedAccess.configuration


initialToMain : Initial -> Maybe Main
initialToMain i =
    Maybe.map3
        (\dashboards projectsByDashboard tasksByProjectId ->
            { jwt = i.jwt
            , dashboards = dashboards
            , projectsByDashboard = projectsByDashboard
            , tasksByProjectId = tasksByProjectId
            , search = ""
            }
        )
        i.dashboards
        i.projectsByDashboard
        i.tasksByProjectId


lenses :
    { initial :
        { dashboards : Lens Initial (Maybe (List Dashboard))
        , projectsByDashboard : Lens Initial (Maybe (DictList DashboardId (List Project)))
        , tasksByProjectId : Lens Initial (Maybe (DictList ProjectId (List Task)))
        }
    , main :
        { dashboards : Lens Main (List Dashboard)
        , projectsByDashboard : Lens Main (DictList DashboardId (List Project))
        , tasksByProjectId : Lens Main (DictList ProjectId (List Task))
        }
    }
lenses =
    { initial =
        { dashboards = Lens .dashboards (\b a -> { a | dashboards = b })
        , projectsByDashboard = Lens .projectsByDashboard (\b a -> { a | projectsByDashboard = b })
        , tasksByProjectId = Lens .tasksByProjectId (\b a -> { a | tasksByProjectId = b })
        }
    , main =
        { dashboards = Lens .dashboards (\b a -> { a | dashboards = b })
        , projectsByDashboard = Lens .projectsByDashboard (\b a -> { a | projectsByDashboard = b })
        , tasksByProjectId = Lens .tasksByProjectId (\b a -> { a | tasksByProjectId = b })
        }
    }


type alias Flags =
    { authorizedAccess : AuthorizedAccess }


type LogicMsg
    = FetchDashboards
    | GotFetchDashboardsResponse (HttpUtil.GraphQLResult (List Dashboard))
    | FetchProjects
    | GotFetchProjectsResponse (HttpUtil.GraphQLResult (List Project))
    | FetchTasks
    | GotFetchTasksResponse (HttpUtil.GraphQLResult (List Task))
    | SetSearchString String
    | EditTask TaskId TaskUpdate
    | SaveEditTask TaskId
    | GotSaveEditTaskResponse (HttpUtil.GraphQLResult Task)
    | ToggleControls TaskId
    | EnterEditTask TaskId
    | ExitEditTask TaskId
