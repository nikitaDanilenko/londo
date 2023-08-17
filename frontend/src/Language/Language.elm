module Language.Language exposing (..)


type alias Language =
    { requestRegistration : RequestRegistration
    , confirmRegistration : ConfirmRegistration
    , userCreation : UserCreation
    , login : Login
    , overview : Overview
    , taskEditor : TaskEditor
    , projectEditor : ProjectEditor
    , dashboardEditor : DashboardEditor
    , dashboardEntryEditor : DashboardEntryEditor
    , statistics : Statistics
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
    , percent : String
    , fraction : String
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
    , save : String
    , cancel : String
    }


type alias DashboardEditor =
    { add : String
    , newDashboard : String
    , delete : String
    , confirmDelete : String
    , edit : String
    , dashboardEntryEditor : String
    , header : String
    , description : String
    , visibility : String
    , save : String
    , cancel : String
    }


type alias DashboardEntryEditor =
    { dashboardEntries : String
    , projects : String
    , newDashboardEntry : String
    , delete : String
    , confirmDelete : String
    , dashboardEntryName : String
    , dashboardEntryDescription : String
    , taskEditor : String
    , create : String
    , edit : String
    , cancel : String
    , save : String
    , select : String
    , add : String
    , added : String
    }


type alias Statistics =
    { total : String
    , counted : String
    , simulated : String
    , reachableAll : String
    , reachedAll : String
    , meanAbsolute : String
    , meanRelativeExact : String
    , meanRelativeFloored : String
    , meanExact : String
    , meanFloored : String
    , differenceOneExactTotal : String
    , differenceOneExactCounted : String
    , differenceOneFlooredTotal : String
    , differenceOneFlooredCounted : String
    , differenceCompleteExactTotal : String
    , differenceCompleteExactCounted : String
    }


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
        { tasks = "Tasks"
        , newTask = "Create new task"
        , delete = "Delete"
        , confirmDelete = "Delete?"
        , taskName = "Name"
        , counting = "Counting?"
        , taskKind = "Kind"
        , discrete = "Discrete"
        , percent = "Percent"
        , fraction = "Fraction"
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
        , taskEditor = "Tasks"
        , name = "Name"
        , description = "Description"
        , save = "Save"
        , cancel = "Cancel"
        }
    , dashboardEditor =
        { add = "Add"
        , newDashboard = "New dashboard"
        , delete = "Delete"
        , confirmDelete = "Delete?"
        , edit = "Edit"
        , dashboardEntryEditor = "Entries"
        , header = "Name"
        , description = "Description"
        , visibility = "Visibility"
        , save = "Save"
        , cancel = "Cancel"
        }
    , dashboardEntryEditor =
        { dashboardEntries = "Dashboard entries"
        , projects = "Projects"
        , newDashboardEntry = "New dashboard entry"
        , delete = "Delete"
        , confirmDelete = "Delete?"
        , dashboardEntryName = "Name"
        , dashboardEntryDescription = "Description"
        , taskEditor = "Project"
        , create = "Create"
        , edit = "Edit"
        , cancel = "Cancel"
        , save = "Save"
        , select = "Select"
        , add = "Add"
        , added = "Added"
        }
    , statistics =
        { total = "Total"
        , counted = "Counted"
        , simulated = "Simulated"
        , reachableAll = "Reachable (all)"
        , reachedAll = "Reached (all)"
        , meanAbsolute = "Mean (absolute)"
        , meanRelativeExact = "Mean (relative, exact)"
        , meanRelativeFloored = "Mean (relative, floored)"
        , meanExact = "Mean (exact)"
        , meanFloored = "Mean (floored)"
        , differenceOneExactTotal = "Δ (exact, total)"
        , differenceOneExactCounted = "Δ (exact, counted)"
        , differenceOneFlooredTotal = "Δ (floored, total)"
        , differenceOneFlooredCounted = "Δ (floored, counted)"
        , differenceCompleteExactTotal = "After completion (total)"
        , differenceCompleteExactCounted = "After completion (counted)"
        }
    }


fromString : String -> Language
fromString name =
    case name of
        "en" ->
            english

        _ ->
            default
