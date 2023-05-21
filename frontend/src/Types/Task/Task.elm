module Types.Task.Task exposing (..)

import Graphql.Http
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.Mutation
import LondoGQL.Object
import LondoGQL.Object.Task
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Progress.Progress
import Types.Task.TaskId
import Util.HttpUtil as HttpUtil


type alias Task =
    { id : Types.Task.TaskId.TaskId
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
        (LondoGQL.Object.Task.id Types.Task.TaskId.selection)
        LondoGQL.Object.Task.name
        LondoGQL.Object.Task.taskKind
        LondoGQL.Object.Task.unit
        (LondoGQL.Object.Task.progress Types.Progress.Progress.selection)
        LondoGQL.Object.Task.counting


deleteWith :
    (Types.Task.TaskId.TaskId -> HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> Types.Task.TaskId.TaskId
    -> Cmd msg
deleteWith expect authorizedAccess taskId =
    LondoGQL.Mutation.deleteTask
        { input =
            { taskId = taskId |> Types.Task.TaskId.toGraphQLInput
            }
        }
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect taskId
            }
