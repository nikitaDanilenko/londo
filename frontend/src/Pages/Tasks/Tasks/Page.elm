module Pages.Tasks.Tasks.Page exposing (..)

import Language.Language
import Monocle.Lens exposing (Lens)
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Types.Project.ProjectId exposing (ProjectId)
import Types.Task.Creation
import Types.Task.Task
import Types.Task.TaskId
import Types.Task.Update


type alias Model =
    Tristate.Model Main Initial


type alias TaskId =
    Types.Task.TaskId.TaskId


type alias Task =
    Types.Task.Task.Task


type alias Creation =
    Types.Task.Creation.ClientInput


type alias Update =
    Types.Task.Update.ClientInput


type alias Language =
    Language.Language.TaskEditor


type alias Initial =
    { projectId : ProjectId
    , initial : Pages.Util.ParentEditor.Page.Initial TaskId Task Update Language
    }


type alias Main =
    { projectId : ProjectId
    , main : Pages.Util.ParentEditor.Page.Main TaskId Task Creation Update Language
    }


lenses :
    { initial : Lens Initial (Pages.Util.ParentEditor.Page.Initial TaskId Task Update Language)
    , main : Lens Main (Pages.Util.ParentEditor.Page.Main TaskId Task Creation Update Language)
    }
lenses =
    { initial = Lens .initial (\b a -> { a | initial = b })
    , main = Lens .main (\b a -> { a | main = b })
    }


initial : JWT -> ProjectId -> Initial
initial jwt projectId =
    { projectId = projectId
    , initial = Pages.Util.ParentEditor.Page.defaultInitial jwt Language.Language.default.taskEditor
    }


initialToMain : Initial -> Maybe Main
initialToMain i =
    Pages.Util.ParentEditor.Page.initialToMain i.initial
        |> Maybe.map (Main i.projectId)


type alias Msg =
    Tristate.Msg LogicMsg


type alias LogicMsg =
    Pages.Util.ParentEditor.Page.LogicMsg TaskId Task Creation Update
