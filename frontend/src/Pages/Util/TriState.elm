module Pages.Util.TriState exposing (..)

import Maybe.Extra exposing (unwrap)
import RemoteData exposing (RemoteData)


type TriState
    = Initial
    | Success
    | Failure


fromRemoteData : RemoteData e a -> TriState
fromRemoteData =
    RemoteData.toMaybe >> unwrap Failure (\_ -> Success)
