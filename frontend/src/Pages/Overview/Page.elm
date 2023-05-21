module Pages.Overview.Page exposing (..)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Language.Language as Language
import Pages.View.Tristate as Tristate


type alias Model =
    Tristate.Model Main Initial


type alias Main =
    { language : Language.Overview }


type alias Initial =
    ()


initial : Configuration -> Model
initial =
    flip Tristate.createMain
        { language = Language.default.overview
        }


type alias Msg =
    Tristate.Msg LogicMsg


type alias LogicMsg =
    ()


type alias Flags =
    { configuration : Configuration
    }
