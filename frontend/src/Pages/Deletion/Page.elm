module Pages.Deletion.Page exposing (..)

import Configuration exposing (Configuration)
import Language.Language as Language
import Monocle.Lens exposing (Lens)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT, UserIdentifier)
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main ()


type alias Main =
    { deletionJWT : JWT
    , userIdentifier : UserIdentifier
    , mode : Mode
    , language : Language
    }


type alias Language =
    Language.AccountDeletion


initial : Flags -> Model
initial flags =
    { deletionJWT = flags.deletionJWT
    , userIdentifier = flags.userIdentifier
    , mode = Checking
    , language = Language.default.accountDeletion
    }
        |> Tristate.createMain flags.configuration Language.default.errorHandling


lenses :
    { main :
        { mode : Lens Main Mode
        }
    }
lenses =
    { main =
        { mode = Lens .mode (\b a -> { a | mode = b })
        }
    }


type Mode
    = Checking
    | Confirmed


type alias Flags =
    { configuration : Configuration
    , userIdentifier : UserIdentifier
    , deletionJWT : JWT
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = Confirm
    | GotConfirmResponse (HttpUtil.GraphQLResult Bool)
