module Types.User.Login exposing (..)

import Configuration exposing (Configuration)
import Graphql.Http
import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Types.Auxiliary exposing (JWT)
import Util.HttpUtil as HttpUtil


type alias ClientInput =
    { nickname : String
    , password : String
    , isValidityUnrestricted : Bool
    }


initial : ClientInput
initial =
    { nickname = ""
    , password = ""
    , isValidityUnrestricted = False
    }


lenses :
    { nickname : Lens ClientInput String
    , password : Lens ClientInput String
    , isValidityUnrestricted : Lens ClientInput Bool
    }
lenses =
    { nickname = Lens .nickname (\b a -> { a | nickname = b })
    , password = Lens .password (\b a -> { a | password = b })
    , isValidityUnrestricted = Lens .isValidityUnrestricted (\b a -> { a | isValidityUnrestricted = b })
    }


toGraphQLInput : ClientInput -> LondoGQL.InputObject.LoginInput
toGraphQLInput input =
    input


loginWith :
    (HttpUtil.GraphQLResult JWT -> msg)
    -> Configuration
    -> ClientInput
    -> Cmd msg
loginWith expect configuration input =
    LondoGQL.Mutation.login
        { input = input }
        |> Graphql.Http.mutationRequest configuration.graphQLEndpoint
        |> Graphql.Http.send expect
