module Types.Task.Creation exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (ProgressInput)
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)
import Types.Progress.Input


type alias ClientInput =
    { name : ValidatedInput String
    , taskKind : TaskKind
    , unit : Maybe String
    , progress : Types.Progress.Input.ClientInput
    , counting : Bool
    }


default : ClientInput
default =
    { name = ValidatedInput.nonEmptyString
    , taskKind = LondoGQL.Enum.TaskKind.Percentual
    , unit = Nothing
    , progress = Types.Progress.Input.default
    , counting = True
    }


toGraphQLInput : ClientInput -> LondoGQL.InputObject.TaskCreation
toGraphQLInput clientInput =
    { name = clientInput.name.value
    , taskKind = clientInput.taskKind
    , unit = clientInput.unit |> OptionalArgument.fromMaybe
    , progress = clientInput.progress |> Types.Progress.Input.toGraphQLInput
    , counting = clientInput.counting
    }
