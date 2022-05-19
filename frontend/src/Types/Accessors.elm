module Types.Accessors exposing (..)

import List.Nonempty as NE exposing (Nonempty)
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


everybody : Representation
everybody =
    { isAllowList = False
    , userIds = Nothing
    }


nobody : Representation
nobody =
    { isAllowList = True
    , userIds = Nothing
    }


only : NE.Nonempty UserId -> Representation
only us =
    { isAllowList = True
    , userIds = Just us
    }


except : NE.Nonempty UserId -> Representation
except us =
    { isAllowList = False
    , userIds = Just us
    }