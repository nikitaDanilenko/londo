module Language.Language exposing (..)


type alias Language =
    { requestRegistration : RequestRegistration
    , confirmRegistration : ConfirmRegistration
    , userCreation : UserCreation
    , login : Login
    , overview : Overview
    , taskEditor : TaskEditor
    , projectEditor : ProjectEditor
    , searchProject : SearchProject
    }


type alias RequestRegistration =
    { header : String
    , nickname : String
    , email : String
    , register : String
    , registrationSuccessful : String
    , registrationFailed : String
    , mainPage : String
    }


type alias ConfirmRegistration =
    { header : String
    , nickname : String
    , email : String
    , displayName : String
    , password : String
    , passwordRepetition : String
    , confirm : String
    , successfullyCreatedUser : String
    , mainPage : String
    }


type alias UserCreation =
    { nickname : String
    , password1 : String
    , password2 : String
    , createUser : String
    , success : String
    , failure : String
    , loginPageLinkText : String
    , tryAgain : String
    , create : String
    }


type alias Login =
    { nickname : String
    , password : String
    , login : String
    , keepMeLoggedIn : String
    , createAccount : String
    , recoverAccount : String
    }


type alias Overview =
    { dashboards : String
    , projects : String
    , settings : String
    }


type alias TaskEditor =
    { tasks : String
    , newTask : String
    , delete : String
    , confirmDelete : String
    , taskName : String
    , counting : String
    , taskKind : String
    , discrete : String
    , percentual : String
    , fractional : String
    , unit : String
    , progress : String
    , projectName : String
    , create : String
    , edit : String
    , cancel : String
    , save : String
    }


type alias ProjectEditor =
    { add : String
    , newProject : String
    , delete : String
    , confirmDelete : String
    , edit : String
    , taskEditor : String
    , name : String
    , description : String
    , flatIfSingleTask : String
    , save : String
    , cancel : String
    }


type alias SearchProject =
    {}


default : Language
default =
    english


english : Language
english =
    { requestRegistration =
        { header = "Registration"
        , nickname = "Nickname"
        , email = "Email for registration"
        , register = "Request registration"
        , registrationSuccessful = "Successfully requested token! Check your email to proceed!"
        , registrationFailed = "There was an error requesting the token. Please try again!"
        , mainPage = "Main page"
        }
    , confirmRegistration =
        { header = "Confirm registration"
        , nickname = "Nickname"
        , email = "Email"
        , displayName = "Display name (optional)"
        , password = "Password"
        , passwordRepetition = "Password repetition"
        , confirm = "Confirm"
        , successfullyCreatedUser = "User creation successful"
        , mainPage = "Main Page"
        }
    , userCreation =
        { nickname = "User name"
        , password1 = "Password"
        , password2 = "Password repetition"
        , createUser = "Create user"
        , success = "User created successfully"
        , failure = "User creation failed"
        , loginPageLinkText = "Go to login page"
        , tryAgain = "Try again"
        , create = "Create"
        }
    , login =
        { nickname = "User name"
        , password = "Password"
        , login = "Log In"
        , keepMeLoggedIn = "Keep me logged in"
        , createAccount = "Create account"
        , recoverAccount = "Recover account"
        }
    , overview =
        { dashboards = "Dashboards"
        , projects = "Projects"
        , settings = "Settings"
        }
    , taskEditor =
        { tasks = "Plain tasks"
        , newTask = "Create new task"
        , delete = "Delete"
        , confirmDelete = "Delete?"
        , taskName = "Name"
        , counting = "Counting?"
        , taskKind = "Kind"
        , discrete = "Discrete"
        , percentual = "Percentual"
        , fractional = "Fractional"
        , unit = "Unit"
        , progress = "Progress"
        , projectName = "Project name"
        , create = "Create"
        , edit = "Edit"
        , cancel = "Cancel"
        , save = "Save"
        }
    , projectEditor =
        { add = "Add"
        , newProject = "New project"
        , delete = "Delete"
        , confirmDelete = "Delete?"
        , edit = "Edit"
        , taskEditor = "Task editor"
        , name = "Project name"
        , description = "Description"
        , flatIfSingleTask = "Flatten if the project contains only a single task"
        , save = "Save"
        , cancel = "Cancel"
        }
    , searchProject = {}
    }


fromString : String -> Language
fromString name =
    case name of
        "en" ->
            english

        _ ->
            default
