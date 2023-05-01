module Pages.Util.Style exposing (..)

import Html exposing (Attribute)
import Html.Attributes exposing (class, id)


classes :
    { button :
        { cancel : Attribute msg
        , confirm : Attribute msg
        , error : Attribute msg
        , menu : Attribute msg
        , navigation : Attribute msg
        , overview : Attribute msg
        , pager : Attribute msg
        }
    , disabled : Attribute msg
    , editable : Attribute msg
    , ellipsis : Attribute msg
    , info : Attribute msg
    , search :
        { area : Attribute msg
        , field : Attribute msg
        }
    , toggle : Attribute msg
    }
classes =
    { button =
        { cancel = class "cancel-button"
        , confirm = class "confirm-button"
        , error = class "error-button"
        , menu = class "menu-button"
        , navigation = class "navigation-button"
        , overview = class "overview-button"
        , pager = class "pager-button"
        }
    , disabled = class "disabled"
    , editable = class "editable"
    , ellipsis = class "ellipsis"
    , info = class "info"
    , search =
        { area = class "search-area"
        , field = class "search-field"
        }
    , toggle = class "toggle"
    }


ids :
    { confirmRegistration : Attribute msg
    , error : Attribute msg
    , login : Attribute msg
    , navigation : Attribute msg
    , overview : Attribute msg
    , registrationRequestSent : Attribute msg
    , requestRegistration : Attribute msg
    }
ids =
    { confirmRegistration = id "confirm-registration"
    , error = id "error"
    , login = id "login"
    , navigation = id "navigation"
    , overview = id "overview"
    , registrationRequestSent = id "registration-request-sent"
    , requestRegistration = id "request-registration"
    }
