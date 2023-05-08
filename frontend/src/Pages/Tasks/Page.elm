module Pages.Tasks.Page exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Tasks.Project.Page
import Pages.Tasks.Tasks.Page
import Pages.Util.Parent.Page
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Types.Project.Resolved
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main Initial


type alias Main =
    { jwt : JWT
    , project : Pages.Tasks.Project.Page.Main
    , tasks : Pages.Tasks.Tasks.Page.Main
    }


type alias Initial =
    { jwt : JWT
    , project : Pages.Tasks.Project.Page.Initial
    , tasks : Pages.Tasks.Tasks.Page.Initial
    }


initialToMain : Initial -> Maybe Main
initialToMain i =
    i.project
        |> Pages.Util.Parent.Page.initialToMain
        |> Maybe.andThen
            (\project ->
                i.tasks.initial
                    |> Pages.Util.ParentEditor.Page.initialToMain
                    |> Maybe.map
                        (\tasks ->
                            { jwt = i.jwt
                            , project = project
                            , tasks =
                                { projectId = i.tasks.projectId
                                , main = tasks
                                }
                            }
                        )
            )


lenses :
    { initial :
        { project : Lens Initial Pages.Tasks.Project.Page.Initial
        , tasks : Lens Initial Pages.Tasks.Tasks.Page.Initial
        }
    , main :
        { project : Lens Main Pages.Tasks.Project.Page.Main
        , tasks : Lens Main Pages.Tasks.Tasks.Page.Main
        }
    }
lenses =
    { initial =
        { project = Lens .project (\b a -> { a | project = b })
        , tasks = Lens .tasks (\b a -> { a | tasks = b })
        }
    , main =
        { project = Lens .project (\b a -> { a | project = b })
        , tasks = Lens .tasks (\b a -> { a | tasks = b })
        }
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = GotFetchResponse (HttpUtil.GraphQLResult Types.Project.Resolved.Resolved)
    | ProjectMsg Pages.Tasks.Project.Page.LogicMsg
    | TasksMsg Pages.Tasks.Tasks.Page.LogicMsg
