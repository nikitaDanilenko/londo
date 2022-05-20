module Util.LensUtil exposing (..)

import List.Extra
import Monocle.Optional exposing (Optional)


firstSuch : (a -> Bool) -> Optional (List a) a
firstSuch p =
    { getOption = List.Extra.find p
    , set = List.Extra.setIf p
    }
