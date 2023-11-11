module Pages.Recovery.Request.Page exposing (..)

import Configuration exposing (Configuration)
import Language.Language as Language
import LondoGQL.Scalar
import Monocle.Lens exposing (Lens)
import Pages.View.Tristate as Tristate
import Types.User.Id
import Types.User.SearchResult
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main Initial


type alias Main =
    { users : List Types.User.SearchResult.SearchResult
    , searchString : String
    , mode : Mode
    , language : Language
    }


type alias Initial =
    ()


type alias Language =
    Language.RequestAccountRecovery


initial : Configuration -> Model
initial configuration =
    { users = []
    , searchString = ""
    , mode = Initial
    , language = Language.default.requestAccountRecovery
    }
        |> Tristate.createMain configuration


type Mode
    = Initial
    | Requesting
    | Requested


lenses :
    { main :
        { users : Lens Main (List Types.User.SearchResult.SearchResult)
        , searchString : Lens Main String
        , mode : Lens Main Mode
        }
    }
lenses =
    { main =
        { users = Lens .users (\b a -> { a | users = b })
        , searchString = Lens .searchString (\b a -> { a | searchString = b })
        , mode = Lens .mode (\b a -> { a | mode = b })
        }
    }


type alias Flags =
    { configuration : Configuration
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = Find
    | GotFindResponse (HttpUtil.GraphQLResult (List Types.User.SearchResult.SearchResult))
    | SetSearchString String
    | RequestRecovery Types.User.Id.Id
    | GotRequestRecoveryResponse (HttpUtil.GraphQLResult LondoGQL.Scalar.Unit)
