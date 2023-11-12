module Types.User.SearchResult exposing (..)

import Configuration exposing (Configuration)
import Graphql.Http
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.FindUserResult
import LondoGQL.Query
import Types.User.Id
import Util.HttpUtil as HttpUtil


type alias SearchResult =
    { id : Types.User.Id.Id
    , nickname : String
    }


selection : SelectionSet SearchResult LondoGQL.Object.FindUserResult
selection =
    SelectionSet.map2 SearchResult
        (LondoGQL.Object.FindUserResult.id Types.User.Id.selection)
        LondoGQL.Object.FindUserResult.nickname


fetchWith :
    (HttpUtil.GraphQLResult (List SearchResult) -> msg)
    -> Configuration
    -> String
    -> Cmd msg
fetchWith expect configuration identifier =
    LondoGQL.Query.findUser
        { input =
            { searchString = identifier
            }
        }
        selection
        |> Graphql.Http.queryRequest configuration.graphQLEndpoint
        |> Graphql.Http.send expect
