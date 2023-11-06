module Pages.Registration.Confirm.Page exposing (..)

import Configuration exposing (Configuration)
import Language.Language as Language
import LondoGQL.Scalar exposing (Unit)
import Monocle.Lens exposing (Lens)
import Pages.Util.ComplementInput as ComplementInput exposing (ComplementInput)
import Pages.View.Tristate as Tristate
import Types.Auxiliary exposing (JWT, UserIdentifier)
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main ()


type alias Main =
    { userIdentifier : UserIdentifier
    , complementInput : ComplementInput
    , registrationJWT : JWT
    , mode : Mode
    , language : Language.ConfirmRegistration
    }


initial : Flags -> Model
initial flags =
    { userIdentifier = flags.userIdentifier
    , complementInput = ComplementInput.initial
    , registrationJWT = flags.registrationJWT
    , mode = Editing
    , language = Language.default.confirmRegistration
    }
        |> Tristate.createMain flags.configuration


lenses :
    { main :
        { complementInput : Lens Main ComplementInput
        , mode : Lens Main Mode
        }
    }
lenses =
    { main =
        { complementInput = Lens .complementInput (\b a -> { a | complementInput = b })
        , mode = Lens .mode (\b a -> { a | mode = b })
        }
    }


type Mode
    = Editing
    | Confirmed


type alias Flags =
    { configuration : Configuration
    , userIdentifier : UserIdentifier
    , registrationJWT : JWT
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = SetComplementInput ComplementInput
    | Request
    | GotResponse (HttpUtil.GraphQLResult Unit)
