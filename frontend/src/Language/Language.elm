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
    , userSettings : UserSettings
    , accountDeletion : AccountDeletion
    , accountRecovery : AccountRecovery
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
    , counting : String
    , simulatedTotal : String
    , simulatedCounting : String
    , reachableAll : String
    , reachedAll : String
    , meanAbsolute : String
    , meanRelative : String
    , mean : String
    , differenceOneTotal : String
    , differenceOneCounting : String
    , differenceCompleteTotal : String
    , differenceCompleteCounting : String
    , statistics : String
    , simulation : String
    }


type alias UserSettings =
    { userSettings : String
    , nickname : String
    , email : String
    , displayName : String
    , newDisplayName : String
    , updateSettings : String
    , newPassword : String
    , newPasswordRepetition : String
    , updatePassword : String
    , deleteAccount : String
    , logoutThisDevice : String
    , logoutAllDevices : String
    , deletionRequested : String
    , changeSettings : String
    , changePassword : String
    , dangerZone : String
    }


type alias AccountDeletion =
    { confirmDeletion : String
    , nickname : String
    , email : String
    , delete : String
    , cancel : String
    , deletionSuccessful : String
    , mainPage : String
    }


type alias AccountRecovery =
    { requestSuccessful : String
    , mainPage : String
    , recovery : String
    , noAccountFound : String
    , multipleAccountsFound : String
    , find : String
    , identifier : String
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
        , login = "Log in"
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
        , counting = "Counting"
        , simulatedTotal = "Simulated (total)"
        , simulatedCounting = "Simulated (counting)"
        , reachableAll = "Reachable (all)"
        , reachedAll = "Reached (all)"
        , meanAbsolute = "Mean (absolute)"
        , meanRelative = "Mean (relative)"
        , mean = "Mean"
        , differenceOneTotal = "Δ (total)"
        , differenceOneCounting = "Δ (counting)"
        , differenceCompleteTotal = "After completion (total)"
        , differenceCompleteCounting = "After completion (counting)"
        , statistics = "Statistics"
        , simulation = "Simulation"
        }
    , userSettings =
        { userSettings = "User settings"
        , nickname = "Nickname"
        , email = "Email"
        , displayName = "Display name"
        , newDisplayName = "New display name"
        , updateSettings = "Update settings"
        , newPassword = "New password"
        , newPasswordRepetition = "New password repetition"
        , updatePassword = "Update password"
        , deleteAccount = "Delete account"
        , logoutThisDevice = "Logout this device"
        , logoutAllDevices = "Logout all devices"
        , deletionRequested = "Account deletion requested. Please check your email to continue."
        , changeSettings = "Update settings"
        , changePassword = "Change password"
        , dangerZone = "Danger zone"
        }
    , accountDeletion =
        { confirmDeletion = "Confirm deletion"
        , nickname = "Nickname"
        , email = "Email"
        , delete = "Delete"
        , cancel = "Back to main"
        , deletionSuccessful = "Account deletion successful!"
        , mainPage = "Main page"
        }
    , accountRecovery =
        { requestSuccessful = "Requested user recovery. Please check your email."
        , mainPage = "Main page"
        , recovery = "Recovery"
        , noAccountFound = "No matching account found"
        , multipleAccountsFound = "Multiple matching accounts found"
        , find = "Find"
        , identifier = "Nickname or email"
        }
    }


fromString : String -> Language
fromString name =
    case name of
        "en" ->
            english

        _ ->
            default
