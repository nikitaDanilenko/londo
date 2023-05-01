module Util.HttpUtil exposing (..)

import Graphql.Http
import Json.Decode
import Types.Auxiliary exposing (JWT)


type alias ErrorExplanation =
    { cause : String
    , possibleSolution : String
    , redirectToLogin : Bool
    , suggestReload : Bool
    }


errorToExplanation : Graphql.Http.HttpError -> ErrorExplanation
errorToExplanation error =
    case error of
        Graphql.Http.BadUrl string ->
            { cause = "BadUrl: " ++ string
            , possibleSolution = "Check address. If the error persists, please contact an administrator."
            , redirectToLogin = False
            , suggestReload = True
            }

        Graphql.Http.Timeout ->
            { cause = "Timeout"
            , possibleSolution = "Try again later. If the error persists, please contact an administrator."
            , redirectToLogin = False
            , suggestReload = True
            }

        Graphql.Http.NetworkError ->
            { cause = "Timeout"
            , possibleSolution = "Try again later. If the error persists, please contact an administrator."
            , redirectToLogin = False
            , suggestReload = True
            }

        Graphql.Http.BadStatus metadata message ->
            { cause = "BadStatus: " ++ String.fromInt metadata.statusCode ++ " - " ++ message
            , possibleSolution =
                if metadata.statusCode == 401 then
                    "Please log in again to continue."

                else
                    ""
            , redirectToLogin = metadata.statusCode == 401
            , suggestReload = True
            }

        Graphql.Http.BadPayload jsonError ->
            { cause = "Bad payload: " ++ (jsonError |> Json.Decode.errorToString)
            , possibleSolution = ""
            , redirectToLogin = False
            , suggestReload = True
            }


graphQLErrorToExplanation : Graphql.Http.Error a -> ErrorExplanation
graphQLErrorToExplanation error =
    case error of
        Graphql.Http.GraphqlError _ graphqlErrors ->
            { cause =
                graphqlErrors
                    |> List.map .message
                    |> String.join ","
                    |> (++) "Invalid data: "
            , possibleSolution = ""
            , redirectToLogin = False
            , suggestReload = True
            }

        Graphql.Http.HttpError httpError ->
            errorToExplanation httpError


userTokenHeader : String
userTokenHeader =
    "User-Token"


sendWithJWT :
    { jwt : JWT
    , expect : GraphQLResult a -> msg
    }
    -> Graphql.Http.Request a
    -> Cmd msg
sendWithJWT ps =
    Graphql.Http.withHeader userTokenHeader ps.jwt
        >> Graphql.Http.send ps.expect


type alias GraphQLResult a =
    Result (Graphql.Http.Error a) a
