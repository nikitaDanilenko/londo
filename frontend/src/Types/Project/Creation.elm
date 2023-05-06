module Types.Project.Creation exposing (..)

import Graphql.Http
import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject exposing (CreateProjectInput)
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)
import Types.Project.Project
import Util.HttpUtil as HttpUtil


type alias ClientInput =
    { name : ValidatedInput String
    , description : Maybe String
    }


default : ClientInput
default =
    { name = ValidatedInput.nonEmptyString
    , description = Nothing
    }


lenses :
    { name : Lens ClientInput (ValidatedInput String)
    , description : Lens ClientInput (Maybe String)
    }
lenses =
    { name = Lens .name (\b a -> { a | name = b })
    , description = Lens .description (\b a -> { a | description = b })
    }


toCreation : ClientInput -> LondoGQL.InputObject.CreateProjectInput
toCreation input =
    { name = input.name.value
    , description = input.description |> OptionalArgument.fromMaybe
    }


createWith :
    (HttpUtil.GraphQLResult Types.Project.Project.Project -> msg)
    -> AuthorizedAccess
    -> ClientInput
    -> Cmd msg
createWith expect authorizedAccess creation =
    LondoGQL.Mutation.createProject
        { input = creation |> toCreation }
        Types.Project.Project.selection
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }
