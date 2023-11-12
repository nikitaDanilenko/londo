module Util.DictList exposing (DictList, any, empty, filter, fromList, fromListWithKey, get, insert, isEmpty, map, mapWithKey, remove, toList, values)

import Basics.Extra
import List.Extra
import Maybe.Extra
import Monocle.Lens as Lens exposing (Lens)
import Util.Ordering exposing (Ordering)


type DictList k v
    = DictList
        { ordering : Ordering k
        , elements : List ( k, v )
        }


lenses :
    { elements : Lens (DictList k v) (List ( k, v ))
    }
lenses =
    { elements = Lens toList (\elements (DictList ps) -> DictList { ordering = ps.ordering, elements = elements }) }


orderingOf : DictList k v -> Ordering k
orderingOf (DictList ps) =
    ps.ordering


empty : Ordering k -> DictList k v
empty ordering =
    DictList { ordering = ordering, elements = [] }


isEmpty : DictList k v -> Bool
isEmpty =
    toList
        >> List.isEmpty


any : (key -> value -> Bool) -> DictList key value -> Bool
any p =
    toList
        >> List.Extra.find (Basics.Extra.uncurry p)
        >> Maybe.Extra.isJust


fromList : Ordering k -> List ( k, v ) -> DictList k v
fromList ordering list =
    DictList
        { ordering = ordering
        , elements = list |> List.Extra.stableSortWith (\( k1, _ ) ( k2, _ ) -> ordering k1 k2)
        }


toList : DictList k v -> List ( k, v )
toList (DictList kvs) =
    kvs.elements


fromListWithKey : Ordering k -> (v -> k) -> List v -> DictList k v
fromListWithKey ordering keyOf =
    List.map (\v -> ( keyOf v, v )) >> fromList ordering


{-| Insert a key-value pair into a dictionary. If the key is already present, the present value is overwritten.
-}
insert : k -> v -> DictList k v -> DictList k v
insert key value dictList =
    let
        ordering =
            orderingOf dictList

        insertOrdered : List ( k, v ) -> List ( k, v )
        insertOrdered list =
            case list of
                [] ->
                    [ ( key, value ) ]

                ( k, v ) :: rest ->
                    case ordering key k of
                        LT ->
                            ( key, value ) :: list

                        EQ ->
                            ( key, value ) :: rest

                        GT ->
                            ( k, v ) :: insertOrdered rest
    in
    Lens.modify lenses.elements insertOrdered dictList


remove : k -> DictList k v -> DictList k v
remove key dictList =
    let
        ordering =
            orderingOf dictList

        removeOrdered : List ( k, v ) -> List ( k, v )
        removeOrdered list =
            case list of
                [] ->
                    []

                ( k, v ) :: rest ->
                    case ordering key k of
                        LT ->
                            ( k, v ) :: rest

                        EQ ->
                            rest

                        GT ->
                            ( k, v ) :: removeOrdered rest
    in
    Lens.modify lenses.elements removeOrdered dictList


get : k -> DictList k v -> Maybe v
get key (DictList { ordering, elements }) =
    let
        getOrdered : List ( k, v ) -> Maybe v
        getOrdered list =
            case list of
                [] ->
                    Nothing

                ( k, v ) :: rest ->
                    case ordering key k of
                        LT ->
                            Nothing

                        EQ ->
                            Just v

                        GT ->
                            getOrdered rest
    in
    getOrdered elements


filter : (k -> v -> Bool) -> DictList k v -> DictList k v
filter p dictList =
    Lens.modify lenses.elements
        (List.filter (Basics.Extra.uncurry p))
        dictList


values : DictList k v -> List v
values =
    toList
        >> List.map Tuple.second


map : (v -> w) -> DictList k v -> DictList k w
map f dictList =
    DictList
        { ordering = orderingOf dictList
        , elements = List.map (Tuple.mapSecond f) (toList dictList)
        }


mapWithKey : (k -> v -> w) -> DictList k v -> DictList k w
mapWithKey f dictList =
    DictList
        { ordering = orderingOf dictList
        , elements = List.map (\( k, v ) -> ( k, f k v )) (toList dictList)
        }
