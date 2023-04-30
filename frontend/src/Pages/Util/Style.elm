module Pages.Util.Style exposing (..)

import Html exposing (Attribute)
import Html.Attributes exposing (class, id)


classes :
    { button :
        { confirm : Attribute msg
        , error : Attribute msg
        , navigation : Attribute msg
        , pager : Attribute msg
        }
    , disabled : Attribute msg
    , editable : Attribute msg
    , ellipsis : Attribute msg
    , info : Attribute msg
    }
classes =
    { button =
        { confirm = class "confirm-button"
        , error = class "error-button"
        , navigation = class "navigation-button"
        , pager = class "pager-button"
        }
    , disabled = class "disabled"
    , editable = class "editable"
    , ellipsis = class "ellipsis"
    , info = class "info"
    }


ids :
    { error : Attribute msg
    , navigation : Attribute msg
    , registrationRequestSent : Attribute msg
    , requestRegistration : Attribute msg
    }
ids =
    { error = id "error"
    , navigation = id "navigation"
    , registrationRequestSent = id "registration-request-sent"
    , requestRegistration = id "request-registration"
    }
