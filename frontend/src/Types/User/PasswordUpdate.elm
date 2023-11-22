module Types.User.PasswordUpdate exposing (..)

import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.PasswordInput exposing (PasswordInput)
import Util.HttpUtil as HttpUtil


type alias ClientInput =
    { passwordInput : PasswordInput
    }


lenses :
    { passwordInput : Lens ClientInput PasswordInput
    }
lenses =
    { passwordInput = Lens .passwordInput (\b a -> { a | passwordInput = b })
    }


toGraphQLInput : ClientInput -> LondoGQL.InputObject.UpdatePasswordInput
toGraphQLInput input =
    { password = input.passwordInput.password1
    }


updateWith :
    (HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> ClientInput
    -> Cmd msg
updateWith expect authorizedAccess update =
    LondoGQL.Mutation.updatePassword
        { input = update |> toGraphQLInput }
        |> HttpUtil.mutationWith expect authorizedAccess
