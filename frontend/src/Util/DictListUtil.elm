module Util.DictListUtil exposing (..)

import Maybe.Extra
import Util.DictList as DictList exposing (DictList)


existsValue : (v -> Bool) -> DictList key v -> Bool
existsValue p =
    DictList.any (always p)
