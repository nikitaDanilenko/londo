module Types.Accessors exposing (..)

import List.Nonempty exposing (Nonempty)
import Maybe.Extra
import Types.UserId exposing (UserId)


type Accessors
    = Everyone
    | Nobody
    | EveryoneExcept (Nonempty UserId)
    | NobodyExcept (Nonempty UserId)


type alias Representation =
    { isAllowList : Bool
    , userIds : Maybe (Nonempty UserId)
    }


from : Representation -> Accessors
from r =
    r.userIds
        |> (if r.isAllowList then
                Maybe.Extra.unwrap Everyone NobodyExcept

            else
                Maybe.Extra.unwrap Nobody EveryoneExcept
           )


