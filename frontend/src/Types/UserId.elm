module Types.UserId exposing (..)

import LondoGQL.InputObject
import LondoGQL.Scalar exposing (Uuid)


type UserId
    = UserId Uuid


uuid : UserId -> Uuid
uuid (UserId u) =
    u


toInput : UserId -> LondoGQL.InputObject.UserIdInput
toInput =
    uuid >> LondoGQL.InputObject.UserIdInput
