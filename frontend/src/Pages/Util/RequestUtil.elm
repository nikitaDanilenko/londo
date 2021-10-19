module Pages.Util.RequestUtil exposing (..)

import Graphql.Http
import Graphql.Operation exposing (RootMutation)
import Graphql.SelectionSet exposing (SelectionSet)
import RemoteData


sendGraphQLRequest :
    { endpoint : String
    , gotResponse : RemoteData.RemoteData (Graphql.Http.Error a) a -> msg
    }
    -> SelectionSet a RootMutation
    -> Cmd msg
sendGraphQLRequest params =
    Graphql.Http.mutationRequest params.endpoint
        >> Graphql.Http.send (RemoteData.fromResult >> params.gotResponse)
