module Pages.Util.AuthorizedAccess exposing (..)

import Configuration exposing (Configuration)
import Types.Auxiliary exposing (JWT)


type alias AuthorizedAccess =
    { configuration : Configuration
    , jwt : JWT
    }
