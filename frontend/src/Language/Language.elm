module Language.Language exposing (..)


type alias Language =
    { enterEmailForRegistrationRequest : String
    , requestTokenForRegistration : String
    , tokenRequestSuccessful: String
    , tokenRequestFailed: String
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
    }


fromString : String -> Language
fromString name =
    case name of
        "en" ->
            english

        _ ->
            default
