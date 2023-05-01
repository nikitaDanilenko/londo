module Types.Project.Project exposing (..)

import Graphql.Http
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Mutation
import LondoGQL.Object
import LondoGQL.Object.Project
import LondoGQL.Object.ProjectId
import LondoGQL.Query
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Project.ProjectId exposing (ProjectId(..))
import Util.HttpUtil as HttpUtil


type alias Project =
    { id : ProjectId
    , name : String
    , description : Maybe String
    }


lenses :
    { name : Lens Project String
    , description : Lens Project (Maybe String)
    }
lenses =
    { name = Lens .name (\b a -> { a | name = b })
    , description = Lens .description (\b a -> { a | description = b })
    }


projectIdSelection : SelectionSet ProjectId LondoGQL.Object.Project
projectIdSelection =
    LondoGQL.Object.Project.id LondoGQL.Object.ProjectId.uuid |> SelectionSet.map ProjectId


selection : SelectionSet Project LondoGQL.Object.Project
selection =
    SelectionSet.map3 Project
        projectIdSelection
        LondoGQL.Object.Project.name
        LondoGQL.Object.Project.description


fetchWith :
    (HttpUtil.GraphQLResult Project -> msg)
    -> AuthorizedAccess
    -> ProjectId
    -> Cmd msg
fetchWith expect authorizedAccess projectId =
    LondoGQL.Query.fetchProject
        { input =
            { projectId = projectId |> Types.Project.ProjectId.toInput
            }
        }
        selection
        |> Graphql.Http.queryRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }


fetchAllWith :
    (HttpUtil.GraphQLResult (List Project) -> msg)
    -> AuthorizedAccess
    -> Cmd msg
fetchAllWith expect authorizedAccess =
    LondoGQL.Query.fetchAllProjects
        selection
        |> Graphql.Http.queryRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }


deleteWith :
    (Types.Project.ProjectId.ProjectId -> HttpUtil.GraphQLResult Bool -> msg)
    -> AuthorizedAccess
    -> Types.Project.ProjectId.ProjectId
    -> Cmd msg
deleteWith expect authorizedAccess projectId =
    LondoGQL.Mutation.deleteProject
        { input =
            { projectId = projectId |> Types.Project.ProjectId.toInput
            }
        }
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect projectId
            }
