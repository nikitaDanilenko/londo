module Types.Project.Resolved exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.ResolvedProject
import LondoGQL.Query
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Project.Project
import Types.Project.ProjectId exposing (ProjectId)
import Types.Task.Task
import Util.HttpUtil as HttpUtil


type alias Resolved =
    { project : Types.Project.Project.Project
    , tasks : List Types.Task.Task.Task
    }


selection : SelectionSet Resolved LondoGQL.Object.ResolvedProject
selection =
    SelectionSet.map2
        Resolved
        (LondoGQL.Object.ResolvedProject.project Types.Project.Project.selection)
        (LondoGQL.Object.ResolvedProject.tasks Types.Task.Task.selection)


fetchWith :
    (HttpUtil.GraphQLResult Resolved -> msg)
    -> AuthorizedAccess
    -> ProjectId
    -> Cmd msg
fetchWith expect authorizedAccess projectId =
    LondoGQL.Query.fetchResolvedProject
        { input =
            { projectId = projectId |> Types.Project.ProjectId.toGraphQLInput
            }
        }
        selection
        |> HttpUtil.queryWith
            expect
            authorizedAccess
