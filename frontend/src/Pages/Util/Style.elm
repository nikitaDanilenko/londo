module Pages.Util.Style exposing (..)

import Html exposing (Attribute)
import Html.Attributes exposing (class, id)


classes :
    { button :
        { error : Attribute msg
        }
    }
classes =
    { button =
        { error = class "error-button"
        }
    }


ids :
    { error : Attribute msg
    }
ids =
    { error = id "error"
    }
