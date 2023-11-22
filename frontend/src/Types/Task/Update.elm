module Types.Task.Update exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Progress.Input
import Types.Task.Id
import Types.Task.Task
import Util.HttpUtil as HttpUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { name : ValidatedInput String
    , taskKind : TaskKind
    , unit : Maybe String
    , counting : Bool
    , progressUpdate : Types.Progress.Input.ClientInput
    }


lenses :
    { name : Lens ClientInput (ValidatedInput String)
    , taskKind : Lens ClientInput TaskKind
    , unit : Lens ClientInput (Maybe String)
    , counting : Lens ClientInput Bool
    , progressUpdate : Lens ClientInput Types.Progress.Input.ClientInput
    }
lenses =
    { name = Lens .name (\b a -> { a | name = b })
    , taskKind = Lens .taskKind (\b a -> { a | taskKind = b })
    , unit = Lens .unit (\b a -> { a | unit = b })
    , counting = Lens .counting (\b a -> { a | counting = b })
    , progressUpdate = Lens .progressUpdate (\b a -> { a | progressUpdate = b })
    }


from : Types.Task.Task.Task -> ClientInput
from task =
    { name =
        ValidatedInput.nonEmptyString
            |> ValidatedInput.lenses.value.set task.name
            |> ValidatedInput.lenses.text.set task.name
    , taskKind = task.taskKind
    , unit = task.unit
    , counting = task.counting
    , progressUpdate = Types.Progress.Input.from task.progress
    }


toGraphQLInput : ClientInput -> LondoGQL.InputObject.TaskUpdate
toGraphQLInput clientInput =
    { name = clientInput.name.value
    , taskKind = clientInput.taskKind
    , unit = clientInput.unit |> OptionalArgument.fromMaybe
    , counting = clientInput.counting
    , progressUpdate = clientInput.progressUpdate |> Types.Progress.Input.toGraphQLInput
    }


updateWith :
    (HttpUtil.GraphQLResult Types.Task.Task.Task -> msg)
    -> AuthorizedAccess
    -> Types.Task.Id.Id
    -> ClientInput
    -> Cmd msg
updateWith expect authorizedAccess taskId update =
    LondoGQL.Mutation.updateTask
        { input =
            { taskId = taskId |> Types.Task.Id.toGraphQLInput
            , taskUpdate = update |> toGraphQLInput
            }
        }
        Types.Task.Task.selection
        |> HttpUtil.mutationWith expect authorizedAccess
