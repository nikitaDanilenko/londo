module Language.Language exposing (..)


type alias Language =
    { enterEmailForRegistrationRequest : String
    , requestTokenForRegistration : String
    }


default : Language
default =
    english


english : Language
english =
    { enterEmailForRegistrationRequest = "Email for registration"
    , requestTokenForRegistration = "Request registration"
    }


fromString : String -> Language
fromString name =
    case name of
        "en" ->
            english

        _ ->
            default
