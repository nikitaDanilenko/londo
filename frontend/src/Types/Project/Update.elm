module Types.Project.Update exposing (..)

import Graphql.Http
import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Project.Id as ProjectId exposing (Id)
import Types.Project.Project
import Util.HttpUtil as HttpUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { name : ValidatedInput String
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
    { name =
        ValidatedInput.nonEmptyString
            |> ValidatedInput.lenses.value.set project.name
            |> ValidatedInput.lenses.text.set project.name
    , description = project.description
    }


toGraphQLInput : Id -> ClientInput -> LondoGQL.InputObject.UpdateProjectInput
toGraphQLInput projectId input =
    { projectId = projectId |> ProjectId.toGraphQLInput
    , name = input.name.value
    , description = input.description |> OptionalArgument.fromMaybe
    }


updateWith :
    (HttpUtil.GraphQLResult Types.Project.Project.Project -> msg)
    -> AuthorizedAccess
    -> Id
    -> ClientInput
    -> Cmd msg
updateWith expect authorizedAccess projectId update =
    LondoGQL.Mutation.updateProject
        { input = update |> toGraphQLInput projectId }
        Types.Project.Project.selection
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }
