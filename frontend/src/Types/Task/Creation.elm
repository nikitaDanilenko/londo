module Types.Task.Creation exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (ProgressInput)
import Types.Task.Progress exposing (Progress)


type alias ClientInput =
    { name : String
    , taskKind : TaskKind
    , unit : Maybe String
    , progress : Types.Task.Progress.ClientInput
    , counting : Bool
    }


toGraphQLInput : ClientInput -> LondoGQL.InputObject.TaskCreation
toGraphQLInput clientInput =
    { name = clientInput.name
    , taskKind = clientInput.taskKind
    , unit = clientInput.unit |> OptionalArgument.fromMaybe
    , progress = clientInput.progress |> Types.Task.Progress.toGraphQLInput
    , counting = clientInput.counting
    }
