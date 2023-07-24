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
    , dashboard : Dashboard
    , projects : List Project
    , tasksByProjectId : DictList ProjectId (List Task)
    , search : String
    }


type alias Initial =
    { jwt : JWT
    , dashboard : Maybe Dashboard
    , projects : Maybe (List Project)
    , tasksByProjectId : Maybe (DictList ProjectId (List Task))
    }


initial : AuthorizedAccess -> Model
initial authorizedAccess =
    { jwt = authorizedAccess.jwt
    , dashboard = Nothing
    , projects = Nothing
    , tasksByProjectId = Nothing
    }
        |> Tristate.createInitial authorizedAccess.configuration


initialToMain : Initial -> Maybe Main
initialToMain i =
    Maybe.map3
        (\dashboard projects tasksByProjectId ->
            { jwt = i.jwt
            , dashboard = dashboard
            , projects = projects
            , tasksByProjectId = tasksByProjectId
            , search = ""
            }
        )
        i.dashboard
        i.projects
        i.tasksByProjectId


lenses :
    { initial :
        { dashboard : Lens Initial (Maybe Dashboard)
        , projects : Lens Initial (Maybe (List Project))
        , tasksByProjectId : Lens Initial (Maybe (DictList ProjectId (List Task)))
        }
    , main :
        { dashboard : Lens Main Dashboard
        , projects : Lens Main (List Project)
        , tasksByProjectId : Lens Main (DictList ProjectId (List Task))
        }
    }
lenses =
    { initial =
        { dashboard = Lens .dashboard (\b a -> { a | dashboard = b })
        , projects = Lens .projects (\b a -> { a | projects = b })
        , tasksByProjectId = Lens .tasksByProjectId (\b a -> { a | tasksByProjectId = b })
        }
    , main =
        { dashboard = Lens .dashboard (\b a -> { a | dashboard = b })
        , projects = Lens .projects (\b a -> { a | projects = b })
        , tasksByProjectId = Lens .tasksByProjectId (\b a -> { a | tasksByProjectId = b })
        }
    }


type alias Flags =
    { authorizedAccess : AuthorizedAccess }


type LogicMsg
    = GotFetchDashboardsResponse (HttpUtil.GraphQLResult (List Dashboard))
    | GotFetchProjectsResponse (HttpUtil.GraphQLResult (List Project))
    | GotFetchTasksResponse (HttpUtil.GraphQLResult (List Task))
    | SetSearchString String
    | EditTask TaskId TaskUpdate
    | SaveEditTask TaskId
    | GotSaveEditTaskResponse (HttpUtil.GraphQLResult Task)
    | ToggleControls TaskId
    | EnterEditTask TaskId
    | ExitEditTask TaskId


type alias Msg =
    Tristate.Msg LogicMsg
