module Pages.Util.NonEmptyUtil exposing (..)

import List.Nonempty as List


nonEmptyToGraphQL : List.Nonempty a -> { head : a, tail : List a }
nonEmptyToGraphQL ne =
    { head = List.head ne
    , tail = List.tail ne
    }
