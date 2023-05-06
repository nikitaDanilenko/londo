module Pages.Tasks.Tasks.Page exposing (..)

import Language.Language
import Pages.Util.ParentEditor.Page
import Pages.View.Tristate as Tristate
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
    Pages.Util.ParentEditor.Page.Initial TaskId Task Update Language


type alias Main =
    Pages.Util.ParentEditor.Page.Main TaskId Task Creation Update Language


type alias LogicMsg =
    Pages.Util.ParentEditor.Page.LogicMsg TaskId Task Creation Update
