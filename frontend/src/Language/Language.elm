module Language.Language exposing (..)


type alias Language =
    { createRegistrationToken : CreateRegistrationToken
    , userCreation : UserCreation
    , login : Login
    , overview : Overview
    , projectOverview : ProjectOverview
    , newProject : NewProject
    , taskEditor : TaskEditor
    , projectEditor : ProjectEditor
    , searchProject : SearchProject
    }


type alias CreateRegistrationToken =
    { enterEmailForRegistrationRequest : String
    , requestTokenForRegistration : String
    , tokenRequestSuccessful : String
    , tokenRequestFailed : String
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



--todo: Add password recovery parts


type alias Login =
    { nickname : String
    , password : String
    , login : String
    , wrongCombination : String
    , tryAgain : String
    }


type alias Overview =
    { dashboards : String
    , projects : String
    , settings : String
    }


type alias ProjectOverview =
    { myProjects : String
    , details : String
    , newProject : String
    , searchProject : String
    }


type alias NewProject =
    { name : String
    , description : String
    , create : String
    , cancel : String
    , everybody : String
    , readableBy : String
    , writableBy : String
    , nobody : String
    , onlyUsers : String
    , exceptUsers : String
    , flatIfSingleTask : String
    }


type alias TaskEditor =
    { plainTasks : String
    , newPlainTask : String
    , projectReferenceTasks : String
    , newProjectReferenceTask : String
    , weight : String
    , projectReference : String
    , remove : String
    , plainTaskName : String
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
    { newProject : String
    , remove : String
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
    { createRegistrationToken =
        { enterEmailForRegistrationRequest = "Email for registration"
        , requestTokenForRegistration = "Request registration"
        , tokenRequestSuccessful = "Successfully requested token! Check your email to proceed!"
        , tokenRequestFailed = "There was an error requesting the token. Please try again!"
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
        , wrongCombination = "Wrong combination of user name and password"
        , tryAgain = "Try again?"
        }
    , overview =
        { dashboards = "Dashboards"
        , projects = "Projects"
        , settings = "Settings"
        }
    , projectOverview =
        { myProjects = "My projects"
        , details = "Details"
        , newProject = "New project"
        , searchProject = "Search project"
        }
    , newProject =
        { name = "Name"
        , description = "Description"
        , create = "Create"
        , cancel = "Cancel"
        , everybody = "Everybody"
        , readableBy = "Readable by"
        , writableBy = "Editable by"
        , nobody = "Nobody"
        , onlyUsers = "Nobody, except the following people"
        , exceptUsers = "Everybody, except the following people"
        , flatIfSingleTask = "Flatten if the project contains only a single task"
        }
    , taskEditor =
        { plainTasks = "Plain tasks"
        , newPlainTask = "Create new plain task"
        , projectReferenceTasks = "Project reference tasks"
        , newProjectReferenceTask = "Create new project reference task"
        , weight = "Weight"
        , projectReference = "Project reference"
        , remove = "Remove"
        , plainTaskName = "Name"
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
        { newProject = "New project"
        , remove = "Remove"
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
