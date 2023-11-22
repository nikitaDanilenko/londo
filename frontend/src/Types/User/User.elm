module Types.User.User exposing (..)

import Configuration exposing (Configuration)
import Graphql.Http
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Enum.LogoutMode
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
        |> HttpUtil.queryWith expect authorizedAccess


requestDeletionWith :
    (HttpUtil.GraphQLResult LondoGQL.Scalar.Unit -> msg)
    -> AuthorizedAccess
    -> Cmd msg
requestDeletionWith expect authorizedAccess =
    LondoGQL.Mutation.requestDeletion
        |> HttpUtil.mutationWith expect authorizedAccess


confirmDeletionWith :
    (HttpUtil.GraphQLResult Bool -> msg)
    -> Configuration
    -> JWT
    -> Cmd msg
confirmDeletionWith expect configuration deletionToken =
    LondoGQL.Mutation.confirmDeletion
        { input =
            { deletionToken = deletionToken
            }
        }
        |> Graphql.Http.mutationRequest configuration.graphQLEndpoint
        |> Graphql.Http.send expect


requestRecoveryWith :
    (HttpUtil.GraphQLResult LondoGQL.Scalar.Unit -> msg)
    -> Configuration
    -> Types.User.Id.Id
    -> Cmd msg
requestRecoveryWith expect configuration userId =
    LondoGQL.Mutation.requestRecovery
        { input =
            { userId = userId |> Types.User.Id.toGraphQLInput
            }
        }
        |> Graphql.Http.mutationRequest configuration.graphQLEndpoint
        |> Graphql.Http.send expect


confirmRecoveryWith :
    (HttpUtil.GraphQLResult Bool -> msg)
    -> Configuration
    -> JWT
    -> String
    -> Cmd msg
confirmRecoveryWith expect configuration recoveryToken password =
    LondoGQL.Mutation.confirmRecovery
        { input =
            { recoveryToken = recoveryToken
            , password = password
            }
        }
        |> Graphql.Http.mutationRequest configuration.graphQLEndpoint
        |> Graphql.Http.send expect


logoutWith :
    (HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> LondoGQL.Enum.LogoutMode.LogoutMode
    -> Cmd msg
logoutWith expect authorizedAccess logoutMode =
    LondoGQL.Mutation.logout
        { input =
            { logoutMode = logoutMode
            }
        }
        |> HttpUtil.mutationWith expect authorizedAccess
