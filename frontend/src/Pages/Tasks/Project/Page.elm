module Pages.Tasks.Project.Page exposing (..)

import Language.Language
import Pages.Util.Parent.Page
import Pages.View.Tristate as Tristate
import Types.Project.Project
import Types.Project.Update


type alias Model =
    Tristate.Model Main Initial


type alias Project =
    Types.Project.Project.Project


type alias Update =
    Types.Project.Update.ClientInput


type alias Language =
    Language.Language.ProjectEditor


type alias Main =
    Pages.Util.Parent.Page.Main Project Update Language


type alias Initial =
    Pages.Util.Parent.Page.Initial Project Language


type alias LogicMsg =
    Pages.Util.Parent.Page.LogicMsg Project Update
