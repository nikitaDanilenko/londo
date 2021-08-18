module Language.Language exposing (..)


type alias Language =
    { enterEmailForRegistrationRequest : String
    , requestTokenForRegistration : String
    , tokenRequestSuccessful : String
    , tokenRequestFailed : String
    , userCreation : UserCreation
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


type alias Login =
    { nickname : String
    , password : String
    , login : String
    , wrongCombination : String
    , tryAgain : String
    }


default : Language
default =
    english


english : Language
english =
    { enterEmailForRegistrationRequest = "Email for registration"
    , requestTokenForRegistration = "Request registration"
    , tokenRequestSuccessful = "Successfully requested token! Check your email to proceed!"
    , tokenRequestFailed = "There was an error requesting the token. Please try again!"
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
    }


fromString : String -> Language
fromString name =
    case name of
        "en" ->
            english

        _ ->
            default
