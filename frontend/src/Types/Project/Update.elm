module Types.Project.Update exposing (..)

import Graphql.Http
import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)
import Types.Project.Project
import Types.Project.ProjectId as ProjectId exposing (ProjectId)
import Util.HttpUtil as HttpUtil


type alias ClientInput =
    { projectId : ProjectId
    , name : ValidatedInput String
    , description : Maybe String
    }


lenses :
    { name : Lens ClientInput (ValidatedInput String)
    , description : Lens ClientInput (Maybe String)
    }
lenses =
    { name = Lens .name (\b a -> { a | name = b })
    , description = Lens .description (\b a -> { a | description = b })
    }


from : Types.Project.Project.Project -> ClientInput
from project =
    { projectId = project.id
    , name =
        ValidatedInput.nonEmptyString
            |> ValidatedInput.lenses.value.set project.name
            |> ValidatedInput.lenses.text.set project.name
    , description = project.description
    }


to : ClientInput -> LondoGQL.InputObject.UpdateProjectInput
to input =
    { projectId =
        input.projectId
            |> ProjectId.uuid
            |> LondoGQL.InputObject.ProjectIdInput
    , name = input.name.value
    , description = input.description |> OptionalArgument.fromMaybe
    }


updateWith :
    (HttpUtil.GraphQLResult Types.Project.Project.Project -> msg)
    -> AuthorizedAccess
    -> ClientInput
    -> Cmd msg
updateWith expect authorizedAccess update =
    LondoGQL.Mutation.updateProject
        { input = update |> to }
        Types.Project.Project.selection
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }
