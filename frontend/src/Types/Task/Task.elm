module Types.Task.Task exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.Mutation
import LondoGQL.Object
import LondoGQL.Object.Task
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Progress.Progress
import Types.Task.Id
import Util.HttpUtil as HttpUtil


type alias Task =
    { id : Types.Task.Id.Id
    , name : String
    , taskKind : TaskKind
    , unit : Maybe String
    , progress : Types.Progress.Progress.Progress
    , counting : Bool
    }


selection : SelectionSet Task LondoGQL.Object.Task
selection =
    SelectionSet.map6
        Task
        (LondoGQL.Object.Task.id Types.Task.Id.selection)
        LondoGQL.Object.Task.name
        LondoGQL.Object.Task.taskKind
        LondoGQL.Object.Task.unit
        (LondoGQL.Object.Task.progress Types.Progress.Progress.selection)
        LondoGQL.Object.Task.counting


deleteWith :
    (Types.Task.Id.Id -> HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> Types.Task.Id.Id
    -> Cmd msg
deleteWith expect authorizedAccess taskId =
    LondoGQL.Mutation.deleteTask
        { input =
            { taskId = taskId |> Types.Task.Id.toGraphQLInput
            }
        }
        |> HttpUtil.mutationWith (expect taskId) authorizedAccess
