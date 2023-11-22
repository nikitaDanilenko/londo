module Types.Project.Project exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Mutation
import LondoGQL.Object
import LondoGQL.Object.Project
import LondoGQL.Query
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Project.Id
import Util.HttpUtil as HttpUtil


type alias Project =
    { id : Types.Project.Id.Id
    , name : String
    , description : Maybe String
    }


selection : SelectionSet Project LondoGQL.Object.Project
selection =
    SelectionSet.map3 Project
        (LondoGQL.Object.Project.id Types.Project.Id.selection)
        LondoGQL.Object.Project.name
        LondoGQL.Object.Project.description


fetchAllWith :
    (HttpUtil.GraphQLResult (List Project) -> msg)
    -> AuthorizedAccess
    -> Cmd msg
fetchAllWith expect authorizedAccess =
    LondoGQL.Query.fetchAllProjects
        selection
        |> HttpUtil.queryWith expect authorizedAccess


deleteWith :
    (Types.Project.Id.Id -> HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> Types.Project.Id.Id
    -> Cmd msg
deleteWith expect authorizedAccess projectId =
    LondoGQL.Mutation.deleteProject
        { input =
            { projectId = projectId |> Types.Project.Id.toGraphQLInput
            }
        }
        |> HttpUtil.mutationWith (expect projectId) authorizedAccess
