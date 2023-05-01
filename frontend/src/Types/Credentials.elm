module Types.Credentials exposing (..)

import Monocle.Lens exposing (Lens)


type alias Credentials =
    { nickname : String
    , password : String
    , isValidityUnrestricted : Bool
    }


initial : Credentials
initial =
    { nickname = ""
    , password = ""
    , isValidityUnrestricted = False
    }


lenses :
    { nickname : Lens Credentials String
    , password : Lens Credentials String
    , isValidityUnrestricted : Lens Credentials Bool
    }
lenses =
    { nickname = Lens .nickname (\b a -> { a | nickname = b })
    , password = Lens .password (\b a -> { a | password = b })
    , isValidityUnrestricted = Lens .isValidityUnrestricted (\b a -> { a | isValidityUnrestricted = b })
    }
