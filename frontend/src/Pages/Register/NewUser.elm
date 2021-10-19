module Pages.Register.NewUser exposing (..)


type alias NewUser =
    { nickname : String
    , password1 : String
    , password2 : String
    }


empty : NewUser
empty =
    { nickname = ""
    , password1 = ""
    , password2 = ""
    }


type NewUserField
    = UserField
    | PasswordField1
    | PasswordField2


update : NewUser -> NewUserField -> String -> NewUser
update newUser fld text =
    case fld of
        UserField ->
            { newUser | nickname = text }

        PasswordField1 ->
            { newUser | password1 = text }

        PasswordField2 ->
            { newUser | password2 = text }


isValid : NewUser -> Bool
isValid newUser =
    not (String.isEmpty newUser.nickname)
        && newUser.password1
        == newUser.password2
        && not (String.isEmpty newUser.password1)
