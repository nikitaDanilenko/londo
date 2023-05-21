module Util.MaybeUtil exposing (..)

import Maybe.Extra


defined : a -> Maybe a
defined =
    Just


optional : Bool -> a -> Maybe a
optional isDefined =
    Just >> Maybe.Extra.filter (always isDefined)
