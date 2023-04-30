module Pages.Util.Style exposing (..)

import Html exposing (Attribute)
import Html.Attributes exposing (class, id)


classes :
    { button :
        { confirm : Attribute msg
        , error : Attribute msg
        , navigation : Attribute msg
        }
    , editable : Attribute msg
    , info : Attribute msg
    }
classes =
    { button =
        { confirm = class "confirm-button"
        , error = class "error-button"
        , navigation = class "navigation-button"
        }
    , editable = class "editable"
    , info = class "info"
    }


ids :
    { error : Attribute msg
    , registrationRequestSent : Attribute msg
    , requestRegistration : Attribute msg
    }
ids =
    { error = id "error"
    , registrationRequestSent = id "registration-request-sent"
    , requestRegistration = id "request-registration"
    }
