module GraphQLFunctions.OptionalArgumentUtil exposing (..)

import Graphql.OptionalArgument exposing (OptionalArgument(..))


toMaybe : OptionalArgument a -> Maybe a
toMaybe arg =
    case arg of
        Present a ->
            Just a

        _ ->
            Nothing