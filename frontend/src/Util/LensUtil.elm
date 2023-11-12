module Util.LensUtil exposing (..)

import Monocle.Compose as Compose
import Monocle.Lens as Lens exposing (Lens)
import Monocle.Optional as Optional exposing (Optional)
import Util.DictList as DictList exposing (DictList)


dictByKey : key -> Optional (DictList key value) value
dictByKey k =
    { getOption = DictList.get k
    , set = DictList.insert k
    }


dictByKeyWithDefault : key -> value -> Lens (DictList key value) value
dictByKeyWithDefault k value =
    { get = DictList.get k >> Maybe.withDefault value
    , set = DictList.insert k
    }


identityLens : Lens a a
identityLens =
    Lens identity always


updateById : key -> Lens a (DictList key value) -> (value -> value) -> a -> a
updateById id =
    Compose.lensWithOptional (dictByKey id)
        >> Optional.modify


insertAtId : key -> Lens a (DictList key value) -> value -> a -> a
insertAtId id lens =
    DictList.insert id >> Lens.modify lens


deleteAtId : key -> Lens a (DictList key value) -> a -> a
deleteAtId id lens =
    DictList.remove id |> Lens.modify lens
