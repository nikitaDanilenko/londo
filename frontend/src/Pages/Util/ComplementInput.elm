module Pages.Util.ComplementInput exposing (..)

import Monocle.Lens exposing (Lens)
import Pages.Util.PasswordInput as PasswordInput exposing (PasswordInput)


type alias ComplementInput =
    { displayName : Maybe String
    , passwordInput : PasswordInput
    }


initial : ComplementInput
initial =
    { displayName = Nothing
    , passwordInput = PasswordInput.initial
    }


lenses :
    { displayName : Lens ComplementInput (Maybe String)
    , passwordInput : Lens ComplementInput PasswordInput
    }
lenses =
    { displayName = Lens .displayName (\b a -> { a | displayName = b })
    , passwordInput = Lens .passwordInput (\b a -> { a | passwordInput = b })
    }
