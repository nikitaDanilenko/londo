module Pages.Tasks.Page exposing (..)

import Language.Language
import Monocle.Lens exposing (Lens)
import Pages.Tasks.Project.Page
import Pages.Tasks.Tasks.Page
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.Parent.Page
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.Project.Id exposing (Id)
import Types.Project.Resolved
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main Initial


type alias Main =
    { project : Pages.Tasks.Project.Page.Main
    , tasks : Pages.Tasks.Tasks.Page.SubMain
    }


type alias Initial =
    { project : Pages.Tasks.Project.Page.Initial
    , tasks : Pages.Tasks.Tasks.Page.SubInitial
    }


initial : AuthorizedAccess -> Model
initial authorizedAccess =
    { project = Pages.Util.Parent.Page.initialWith authorizedAccess.jwt Language.Language.default.projectEditor
    , tasks = Pages.Tasks.Tasks.Page.subInitial authorizedAccess
    }
        |> Tristate.createInitial authorizedAccess.configuration


initialToMain : Initial -> Maybe Main
initialToMain i =
    i.project
        |> Pages.Util.Parent.Page.initialToMain
        |> Maybe.andThen
            (\project ->
                i.tasks
                    |> Pages.Util.ParentEditor.Page.initialToMain
                    |> Maybe.map
                        (\tasks ->
                            { project = project
                            , tasks = tasks
                            }
                        )
            )


lenses :
    { initial :
        { project : Lens Initial Pages.Tasks.Project.Page.Initial
        , tasks : Lens Initial Pages.Tasks.Tasks.Page.SubInitial
        }
    , main :
        { project : Lens Main Pages.Tasks.Project.Page.Main
        , tasks : Lens Main Pages.Tasks.Tasks.Page.SubMain
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


type alias Flags =
    { projectId : Id
    , authorizedAccess : AuthorizedAccess
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = GotFetchResponse (HttpUtil.GraphQLResult Types.Project.Resolved.Resolved)
    | ProjectMsg Pages.Tasks.Project.Page.LogicMsg
    | TasksMsg Pages.Tasks.Tasks.Page.LogicMsg
