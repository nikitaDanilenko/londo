module Pages.Util.RequestUtil exposing (..)

import Constants
import Graphql.Http
import Graphql.Operation exposing (RootMutation, RootQuery)
import Graphql.SelectionSet exposing (SelectionSet)
import RemoteData exposing (RemoteData)


type alias GraphQLDataOrError a =
    RemoteData (Graphql.Http.Error a) a


type alias GraphQLRequestParameters a msg =
    { endpoint : String
    , token : String
    , gotResponse : GraphQLDataOrError a -> msg
    }

mutateWith : GraphQLRequestParameters a msg -> SelectionSet a RootMutation -> Cmd msg
mutateWith ps =
    Graphql.Http.mutationRequest ps.endpoint
        >> Graphql.Http.withHeader Constants.userToken ps.token
        >> Graphql.Http.send (RemoteData.fromResult >> ps.gotResponse)


queryWith : GraphQLRequestParameters a msg -> SelectionSet a RootQuery -> Cmd msg
queryWith ps =
    Graphql.Http.queryRequest ps.endpoint
        >> Graphql.Http.withHeader Constants.userToken ps.token
        >> Graphql.Http.send (RemoteData.fromResult >> ps.gotResponse)
