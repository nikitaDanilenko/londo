module Types.User.User exposing (..)

import Graphql.Http
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Mutation
import LondoGQL.Object
import LondoGQL.Object.User
import LondoGQL.Query
import LondoGQL.Scalar
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Auxiliary exposing (JWT)
import Types.User.Id
import Util.HttpUtil as HttpUtil


type alias User =
    { id : Types.User.Id.Id
    , nickname : String
    , displayName : Maybe String
    , email : String
    }


selection : SelectionSet User LondoGQL.Object.User
selection =
    SelectionSet.map4 User
        (LondoGQL.Object.User.id Types.User.Id.selection)
        LondoGQL.Object.User.nickname
        LondoGQL.Object.User.displayName
        LondoGQL.Object.User.email


fetchWith :
    (HttpUtil.GraphQLResult User -> msg)
    -> AuthorizedAccess
    -> Cmd msg
fetchWith expect authorizedAccess =
    LondoGQL.Query.fetchUser selection
        |> Graphql.Http.queryRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }


requestDeletionWith :
    (HttpUtil.GraphQLResult LondoGQL.Scalar.Unit -> msg)
    -> AuthorizedAccess
    -> Cmd msg
requestDeletionWith expect authorizedAccess =
    LondoGQL.Mutation.requestDeletion
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }


confirmDeletionWith :
    (HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> JWT
    -> Cmd msg
confirmDeletionWith expect authorizedAccess deletionToken =
    LondoGQL.Mutation.confirmDeletion
        { input =
            { deletionToken = deletionToken
            }
        }
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }


requestRecoveryWith :
    (HttpUtil.GraphQLResult LondoGQL.Scalar.Unit -> msg)
    -> AuthorizedAccess
    -> Types.User.Id.Id
    -> Cmd msg
requestRecoveryWith expect authorizedAccess userId =
    LondoGQL.Mutation.requestRecovery
        { input =
            { userId = userId |> Types.User.Id.toGraphQLInput
            }
        }
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }


confirmRecoveryWith :
    (HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> JWT
    -> String
    -> Cmd msg
confirmRecoveryWith expect authorizedAccess recoveryToken password =
    LondoGQL.Mutation.confirmRecovery
        { input =
            { recoveryToken = recoveryToken
            , password = password
            }
        }
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }
