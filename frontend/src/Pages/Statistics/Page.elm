module Pages.Statistics.Page exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Statistics.Pagination as Pagination exposing (Pagination)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Types.Dashboard.Dashboard
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


type alias EditingResolvedProject =
    { project : Project
    , tasks : Editing Task TaskUpdate
    }


type alias Main =
    { jwt : JWT
    , dashboard : Dashboard
    , projects : DictList ProjectId EditingResolvedProject
    , searchString : String
    , pagination : Pagination
    }


type alias Initial =
    { jwt : JWT
    , dashboard : Maybe Dashboard
    , projects : Maybe (List ResolvedProject)
    }


initial : AuthorizedAccess -> Model
initial authorizedAccess =
    { jwt = authorizedAccess.jwt
    , dashboard = Nothing
    , projects = Nothing
    }
        |> Tristate.createInitial authorizedAccess.configuration


initialToMain : Initial -> Maybe Main
initialToMain i =
    Maybe.map2
        (\dashboard resolvedProjects ->
            { jwt = i.jwt
            , dashboard = dashboard
            , projects =
                resolvedProjects
                    |> List.map
                        (\resolvedProject ->
                            { project = resolvedProject.project
                            , tasks =
                                resolvedProject
                                    |> .tasks
                                    |> Editing.asView
                            }
                        )
                    |> DictList.fromListWithKey (.project >> .id)
            , searchString = ""
            , pagination = Pagination.initial
            }
        )
        i.dashboard
        i.projects


lenses :
    { initial :
        { dashboard : Lens Initial (Maybe Dashboard)
        , projects : Lens Initial (Maybe (List ResolvedProject))
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
        { dashboard = Lens .dashboard (\b a -> { a | dashboard = b })
        , projects = Lens .projects (\b a -> { a | projects = b })
        }
    , main =
        { dashboard = Lens .dashboard (\b a -> { a | dashboard = b })
        , projects = Lens .projects (\b a -> { a | projects = b })
        , searchString = Lens .searchString (\b a -> { a | searchString = b })
        , pagination = Lens .pagination (\b a -> { a | pagination = b })
        }
    }


type alias Flags =
    { authorizedAccess : AuthorizedAccess }


type LogicMsg
    = GotFetchDashboardResponse (HttpUtil.GraphQLResult Dashboard)
    | GotFetchProjectsResponse (HttpUtil.GraphQLResult (List ResolvedProject))
    | SetSearchString String
    | EditTask TaskId TaskUpdate
    | SaveEditTask TaskId
    | GotSaveEditTaskResponse (HttpUtil.GraphQLResult Task)
    | ToggleControls TaskId
    | EnterEditTask TaskId
    | ExitEditTask TaskId
    | SetPagination Pagination
    | SetSearchString String


type alias Msg =
    Tristate.Msg LogicMsg
