module Pages.Util.Style exposing (..)

import Html exposing (Attribute)
import Html.Attributes exposing (class, id)


classes :
    { add : Attribute msg
    , button :
        { add : Attribute msg
        , cancel : Attribute msg
        , confirm : Attribute msg
        , error : Attribute msg
        , menu : Attribute msg
        , navigation : Attribute msg
        , overview : Attribute msg
        , pager : Attribute msg
        }
    , controls : Attribute msg
    , disabled : Attribute msg
    , editable : Attribute msg
    , editing : Attribute msg
    , elementsWithControlsTable : Attribute msg
    , ellipsis : Attribute msg
    , info : Attribute msg
    , pagination : Attribute msg
    , search :
        { area : Attribute msg
        , field : Attribute msg
        }
    , tableHeader : Attribute msg
    , toggle : Attribute msg
    }
classes =
    { add = class "add"
    , button =
        { add = class "add-button"
        , cancel = class "cancel-button"
        , confirm = class "confirm-button"
        , error = class "error-button"
        , menu = class "menu-button"
        , navigation = class "navigation-button"
        , overview = class "overview-button"
        , pager = class "pager-button"
        }
    , controls = class "controls"
    , disabled = class "disabled"
    , editable = class "editable"
    , editing = class "editing"
    , elementsWithControlsTable = class "elements-with-controls-table"
    , ellipsis = class "ellipsis"
    , info = class "info"
    , pagination = class "pagination"
    , search =
        { area = class "search-area"
        , field = class "search-field"
        }
    , tableHeader = class "table-header"
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
