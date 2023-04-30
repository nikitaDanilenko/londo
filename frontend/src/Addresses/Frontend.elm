module Addresses.Frontend exposing (..)

import Addresses.ParserUtil as ParserUtil exposing (AddressWithParser, nicknameEmailParser, with1, with2)
import Pages.Util.ScalarUtil as ScalarUtil
import Types.Auxiliary exposing (JWT, UserIdentifier)
import Types.ProjectId as ProjectId exposing (ProjectId)
import Url.Parser as Parser exposing (s)


requestRegistration : AddressWithParser () a a
requestRegistration =
    plain "request-registration"


requestRecovery : AddressWithParser () a a
requestRecovery =
    plain "request-recovery"


overview : AddressWithParser () a a
overview =
    plain "overview"


login : AddressWithParser () a a
login =
    plain "login"


confirmRegistration : AddressWithParser ( ( String, String ), JWT ) (UserIdentifier -> JWT -> a) a
confirmRegistration =
    confirm "confirm-registration"


deleteAccount : AddressWithParser ( ( String, String ), JWT ) (UserIdentifier -> JWT -> a) a
deleteAccount =
    confirm "delete-account"


confirmRecovery : AddressWithParser ( ( String, String ), JWT ) (UserIdentifier -> JWT -> a) a
confirmRecovery =
    confirm "recover-account"


projects : AddressWithParser () a a
projects =
    plain "projects"


dashboards : AddressWithParser () a a
dashboards =
    plain "dashboards"


userSettings : AddressWithParser () a a
userSettings =
    plain "user-settings"


taskEditor : AddressWithParser ProjectId (ProjectId -> a) a
taskEditor =
    with1
        { step1 = "task-editor"
        , toString = ProjectId.uuid >> ScalarUtil.uuidToString >> List.singleton
        , paramParser = ParserUtil.uuidParser |> Parser.map ProjectId
        }


confirm : String -> AddressWithParser ( ( String, String ), JWT ) (UserIdentifier -> JWT -> a) a
confirm step1 =
    with2
        { step1 = step1
        , toString1 = nicknameEmailParser.address
        , step2 = "token"
        , toString2 = List.singleton
        , paramParser1 = nicknameEmailParser.parser |> Parser.map UserIdentifier
        , paramParser2 = Parser.string
        }


plain : String -> AddressWithParser () a a
plain string =
    { address = always [ string ]
    , parser = s string
    }
