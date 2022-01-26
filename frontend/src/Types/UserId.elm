module Types.UserId exposing (..)

import LondoGQL.InputObject exposing (UserIdInput)
import LondoGQL.Scalar exposing (Uuid)


type UserId
    = UserId Uuid


uuid : UserId -> Uuid
uuid (UserId u) =
    u


toInput : UserId -> UserIdInput
toInput userId =
    { uuid = uuid userId
    }
