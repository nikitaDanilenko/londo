module GraphQLFunctions.ProjectCreationUtil exposing (..)

import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject exposing (AccessorsInput, ProjectCreation)
import Maybe.Extra
import Monocle.Lens exposing (Lens)


name : Lens ProjectCreation String
name =
    Lens .name (\b a -> { a | name = b })


description : Lens ProjectCreation (Maybe String)
description =
    Lens (.description >> OptionalArgumentUtil.toMaybe)
        (\b a ->
            { a
                | description =
                    b
                        |> Maybe.Extra.filter (String.isEmpty >> not)
                        |> OptionalArgument.fromMaybe
            }
        )


flatIfSingleTask : Lens ProjectCreation Bool
flatIfSingleTask =
    Lens .flatIfSingleTask (\b a -> { a | flatIfSingleTask = b })


readAccessors : Lens ProjectCreation AccessorsInput
readAccessors =
    Lens .readAccessors (\b a -> { a | readAccessors = b })


writeAccessors : Lens ProjectCreation AccessorsInput
writeAccessors =
    Lens .writeAccessors (\b a -> { a | writeAccessors = b })
