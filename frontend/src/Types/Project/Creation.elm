module Types.Project.Creation exposing (..)

import Graphql.Http
import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject exposing (CreateProjectInput)
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Project.Project
import Util.HttpUtil as HttpUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


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


toGraphQLInput : ClientInput -> LondoGQL.InputObject.CreateProjectInput
toGraphQLInput input =
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
        { input = creation |> toGraphQLInput }
        Types.Project.Project.selection
        |> HttpUtil.mutationWith expect authorizedAccess
