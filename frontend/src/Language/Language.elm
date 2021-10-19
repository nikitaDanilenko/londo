module Language.Language exposing (..)


type alias Language =
    { createRegistrationToken : CreateRegistrationToken
    , userCreation : UserCreation
    , login : Login
    , overview : Overview
    , projectOverview : ProjectOverview
    , newProject : NewProject
    , editProject : EditProject
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
    }


type alias EditProject =
    {}


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
        }
    , editProject = {}
    , searchProject = {}
    }


fromString : String -> Language
fromString name =
    case name of
        "en" ->
            english

        _ ->
            default
