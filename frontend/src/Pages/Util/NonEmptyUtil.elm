module Pages.Util.NonEmptyUtil exposing (..)

import List.Nonempty as NE


nonEmptyToGraphQL : NE.Nonempty a -> { head : a, tail : List a }
nonEmptyToGraphQL ne =
    { head = NE.head ne
    , tail = NE.tail ne
    }
