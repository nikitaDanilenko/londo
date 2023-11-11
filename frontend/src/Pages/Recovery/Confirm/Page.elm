module Pages.Recovery.Confirm.Page exposing (..)

import Configuration exposing (Configuration)
import Language.Language as Language
import Monocle.Lens exposing (Lens)
import Pages.Util.PasswordInput as PasswordInput exposing (PasswordInput)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT, UserIdentifier)
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main Initial


type alias Main =
    { recoveryJwt : JWT
    , userIdentifier : UserIdentifier
    , passwordInput : PasswordInput
    , mode : Mode
    , language : Language
    }


type alias Initial =
    ()


type alias Language =
    Language.ConfirmAccountRecovery


initial : Flags -> Model
initial flags =
    { recoveryJwt = flags.recoveryJwt
    , userIdentifier = flags.userIdentifier
    , passwordInput = PasswordInput.initial
    , mode = Resetting
    , language = Language.default.confirmAccountRecovery
    }
        |> Tristate.createMain flags.configuration


lenses :
    { main :
        { passwordInput : Lens Main PasswordInput
        , mode : Lens Main Mode
        }
    }
lenses =
    { main =
        { passwordInput = Lens .passwordInput (\b a -> { a | passwordInput = b })
        , mode = Lens .mode (\b a -> { a | mode = b })
        }
    }


type Mode
    = Resetting
    | Confirmed


type alias Flags =
    { configuration : Configuration
    , userIdentifier : UserIdentifier
    , recoveryJwt : JWT
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = SetPasswordInput PasswordInput
    | Confirm
    | GotConfirmResponse (HttpUtil.GraphQLResult Bool)
