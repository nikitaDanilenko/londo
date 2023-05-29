module Pages.Util.Style exposing (..)

import Html exposing (Attribute)
import Html.Attributes exposing (class, id)


classes :
    { add : Attribute msg
    , addElement : Attribute msg
    , addView : Attribute msg
    , button :
        { add : Attribute msg
        , cancel : Attribute msg
        , confirm : Attribute msg
        , delete : Attribute msg
        , edit : Attribute msg
        , editor : Attribute msg
        , error : Attribute msg
        , menu : Attribute msg
        , navigation : Attribute msg
        , overview : Attribute msg
        , pager : Attribute msg
        }
    , choices : Attribute msg
    , controls : Attribute msg
    , dashboardEditTable : Attribute msg
    , disabled : Attribute msg
    , editable : Attribute msg
    , editing : Attribute msg
    , editLine : Attribute msg
    , elementEditTable : Attribute msg
    , elements : Attribute msg
    , elementsWithControlsTable : Attribute msg
    , ellipsis : Attribute msg
    , info : Attribute msg
    , numberCell : Attribute msg
    , numberLabel : Attribute msg
    , pagination : Attribute msg
    , projectEditTable : Attribute msg
    , search :
        { area : Attribute msg
        , field : Attribute msg
        }
    , tableHeader : Attribute msg
    , taskEditTable : Attribute msg
    , toggle : Attribute msg
    }
classes =
    { add = class "add"
    , addElement = class "add-element"
    , addView = class "add-view"
    , button =
        { add = class "add-button"
        , cancel = class "cancel-button"
        , confirm = class "confirm-button"
        , delete = class "delete-button"
        , edit = class "edit-button"
        , editor = class "editor-button"
        , error = class "error-button"
        , menu = class "menu-button"
        , navigation = class "navigation-button"
        , overview = class "overview-button"
        , pager = class "pager-button"
        }
    , choices = class "choices"
    , controls = class "controls"
    , dashboardEditTable = class "dashboard-edit-table"
    , disabled = class "disabled"
    , editable = class "editable"
    , editing = class "editing"
    , editLine = class "edit-line"
    , elementEditTable = class "element-edit-table"
    , elements = class "elements"
    , elementsWithControlsTable = class "elements-with-controls-table"
    , ellipsis = class "ellipsis"
    , info = class "info"
    , numberCell = class "number-cell"
    , numberLabel = class "number-label"
    , pagination = class "pagination"
    , projectEditTable = class "project-edit-table"
    , search =
        { area = class "search-area"
        , field = class "search-field"
        }
    , tableHeader = class "table-header"
    , taskEditTable = class "task-edit-table"
    , toggle = class "toggle"
    }


ids :
    { addDashboardView : Attribute msg
    , addProjectView : Attribute msg
    , addTaskView : Attribute msg
    , confirmRegistration : Attribute msg
    , error : Attribute msg
    , login : Attribute msg
    , navigation : Attribute msg
    , overview : Attribute msg
    , registrationRequestSent : Attribute msg
    , requestRegistration : Attribute msg
    , taskEditor : Attribute msg
    }
ids =
    { addDashboardView = id "add-dashboard-view"
    , addProjectView = id "add-project-view"
    , addTaskView = id "add-task-view"
    , confirmRegistration = id "confirm-registration"
    , error = id "error"
    , login = id "login"
    , navigation = id "navigation"
    , overview = id "overview"
    , registrationRequestSent = id "registration-request-sent"
    , requestRegistration = id "request-registration"
    , taskEditor = id "task-editor"
    }
