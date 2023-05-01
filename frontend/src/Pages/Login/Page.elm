module Pages.Login.Page exposing (..)

import Configuration exposing (Configuration)
import Language.Language as Language
import Monocle.Lens exposing (Lens)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT)
import Types.Credentials as Credentials exposing (Credentials)
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main ()


type alias Main =
    { credentials : Credentials
    , language : Language.Login
    }


initial : Flags -> Model
initial flags =
    { credentials = Credentials.initial
    , language = Language.default.login
    }
        |> Tristate.createMain flags.configuration


lenses :
    { main :
        { credentials : Lens Main Credentials
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
    = SetCredentials Credentials
    | Login
    | GotResponse (HttpUtil.GraphQLResult JWT)
