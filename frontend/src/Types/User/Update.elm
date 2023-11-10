module Types.User.Update exposing (..)

import Graphql.Http
import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.User.User
import Util.HttpUtil as HttpUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { email : ValidatedInput String
    , displayName : Maybe String
    }


lenses :
    { email : Lens ClientInput (ValidatedInput String)
    , displayName : Lens ClientInput (Maybe String)
    }
lenses =
    { email = Lens .email (\b a -> { a | email = b })
    , displayName = Lens .displayName (\b a -> { a | displayName = b })
    }


from : Types.User.User.User -> ClientInput
from user =
    { email =
        ValidatedInput.nonEmptyString
            |> ValidatedInput.lenses.value.set user.email
            |> ValidatedInput.lenses.text.set user.email
    , displayName = user.displayName
    }


toGraphQLInput : ClientInput -> LondoGQL.InputObject.UpdateUserInput
toGraphQLInput input =
    { email = input.email.value
    , displayName = input.displayName |> OptionalArgument.fromMaybe
    }


updateWith :
    (HttpUtil.GraphQLResult Types.User.User.User -> msg)
    -> AuthorizedAccess
    -> ClientInput
    -> Cmd msg
updateWith expect authorizedAccess update =
    LondoGQL.Mutation.updateUser
        { input = update |> toGraphQLInput }
        Types.User.User.selection
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }
