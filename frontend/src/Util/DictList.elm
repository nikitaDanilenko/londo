module Util.DictList exposing (DictList, any, empty, filter, fromList, fromListWithKey, get, insert, isEmpty, map, remove, toList, values)

import Basics.Extra
import List.Extra
import Maybe.Extra


type DictList k v
    = DictList (List ( k, v ))


empty : DictList k v
empty =
    DictList []


isEmpty : DictList k v -> Bool
isEmpty =
    toList
        >> List.isEmpty


any : (key -> a -> Bool) -> DictList key a -> Bool
any p =
    toList
        >> List.Extra.find (Basics.Extra.uncurry p)
        >> Maybe.Extra.isJust


fromList : List ( k, v ) -> DictList k v
fromList =
    DictList


toList : DictList k v -> List ( k, v )
toList (DictList kvs) =
    kvs


fromListWithKey : (v -> k) -> List v -> DictList k v
fromListWithKey keyOf =
    List.map (\v -> ( keyOf v, v )) >> fromList


insert : k -> v -> DictList k v -> DictList k v
insert key value =
    toList
        >> List.Extra.filterNot (\( k, _ ) -> k == key)
        >> (::) ( key, value )
        >> fromList


remove : k -> DictList k v -> DictList k v
remove key =
    toList
        >> List.Extra.filterNot (\( k, _ ) -> k == key)
        >> fromList


get : k -> DictList k v -> Maybe v
get key =
    toList
        >> List.Extra.find (\( k, _ ) -> k == key)
        >> Maybe.map Tuple.second


filter : (k -> v -> Bool) -> DictList k v -> DictList k v
filter p =
    toList
        >> List.filter (Basics.Extra.uncurry p)
        >> fromList


values : DictList k v -> List v
values =
    toList
        >> List.map Tuple.second


map : (v -> w) -> DictList k v -> DictList k w
map f =
    toList
        >> List.map (Tuple.mapSecond f)
        >> fromList
