module Pages.Util.DictListUtil exposing (..)

import Maybe.Extra
import Util.DictList as DictList exposing (DictList)


existsValue : (v -> Bool) -> DictList key v -> Bool
existsValue p =
    DictList.any (always p)


nameOrEmpty : DictList key { a | name : String } -> key -> String
nameOrEmpty map id =
    DictList.get id map |> Maybe.Extra.unwrap "" .name
