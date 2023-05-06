module Pages.Registration.Request.Page exposing (..)

import Configuration exposing (Configuration)
import Language.Language as Language
import LondoGQL.Scalar exposing (Unit)
import Monocle.Lens exposing (Lens)
import Pages.View.Tristate as Tristate
import Util.HttpUtil as HttpUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias Model =
    Tristate.Model Main ()


type alias Main =
    { nickname : ValidatedInput String
    , email : ValidatedInput String
    , mode : Mode
    , language : Language.RequestRegistration
    }


initial : Configuration -> Model
initial configuration =
    { nickname = ValidatedInput.nonEmptyString
    , email = ValidatedInput.nonEmptyString
    , mode = Editing
    , language = Language.default.requestRegistration
    }
        |> Tristate.createMain configuration


lenses :
    { main :
        { nickname : Lens Main (ValidatedInput String)
        , email : Lens Main (ValidatedInput String)
        , mode : Lens Main Mode
        }
    }
lenses =
    { main =
        { nickname = Lens .nickname (\b a -> { a | nickname = b })
        , email = Lens .email (\b a -> { a | email = b })
        , mode = Lens .mode (\b a -> { a | mode = b })
        }
    }


type Mode
    = Editing
    | Confirmed


type alias Flags =
    { configuration : Configuration
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = SetNickname (ValidatedInput String)
    | SetEmail (ValidatedInput String)
    | Request
    | GotRequestResponse (HttpUtil.GraphQLResult Unit)
