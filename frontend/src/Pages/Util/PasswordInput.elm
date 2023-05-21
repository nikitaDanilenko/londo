module Pages.Util.PasswordInput exposing (..)

import Monocle.Lens exposing (Lens)


type alias PasswordInput =
    { password1 : String
    , password2 : String
    }


initial : PasswordInput
initial =
    { password1 = ""
    , password2 = ""
    }


lenses :
    { password1 : Lens PasswordInput String
    , password2 : Lens PasswordInput String
    }
lenses =
    { password1 = Lens .password1 (\b a -> { a | password1 = b })
    , password2 = Lens .password2 (\b a -> { a | password2 = b })
    }


isValidPassword : PasswordInput -> Bool
isValidPassword complementInput =
    complementInput.password1 == complementInput.password2
