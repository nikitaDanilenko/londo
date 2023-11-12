module Pages.Login.Page exposing (..)

import Configuration exposing (Configuration)
import Language.Language as Language
import Monocle.Lens exposing (Lens)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Types.User.Login
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main ()


type alias Main =
    { credentials : Types.User.Login.ClientInput
    , language : Language.Login
    }


initial : Flags -> Model
initial flags =
    { credentials = Types.User.Login.initial
    , language = Language.default.login
    }
        |> Tristate.createMain flags.configuration Language.default.errorHandling


lenses :
    { main :
        { credentials : Lens Main Types.User.Login.ClientInput
        }
    }
lenses =
    { main =
        { credentials = Lens .credentials (\b a -> { a | credentials = b })
        }
    }


type alias Flags =
    { configuration : Configuration
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = SetCredentials Types.User.Login.ClientInput
    | Login
    | GotResponse (HttpUtil.GraphQLResult JWT)
