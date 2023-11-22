module Types.Dashboard.Creation exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.Enum.Visibility
import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Dashboard
import Util.HttpUtil as HttpUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { header : ValidatedInput String
    , description : Maybe String
    , visibility : LondoGQL.Enum.Visibility.Visibility
    }


default : ClientInput
default =
    { header = ValidatedInput.nonEmptyString
    , description = Nothing
    , visibility = LondoGQL.Enum.Visibility.Private
    }


lenses :
    { header : Lens ClientInput (ValidatedInput String)
    , description : Lens ClientInput (Maybe String)
    , visibility : Lens ClientInput LondoGQL.Enum.Visibility.Visibility
    }
lenses =
    { header = Lens .header (\b a -> { a | header = b })
    , description = Lens .description (\b a -> { a | description = b })
    , visibility = Lens .visibility (\b a -> { a | visibility = b })
    }


toGraphQLInput : ClientInput -> LondoGQL.InputObject.CreateDashboardInput
toGraphQLInput input =
    { dashboardCreation =
        { header = input.header.value
        , description = input.description |> OptionalArgument.fromMaybe
        , visibility = input.visibility
        }
    }


createWith :
    (HttpUtil.GraphQLResult Types.Dashboard.Dashboard.Dashboard -> msg)
    -> AuthorizedAccess
    -> ClientInput
    -> Cmd msg
createWith expect authorizedAccess creation =
    LondoGQL.Mutation.createDashboard
        { input = creation |> toGraphQLInput }
        Types.Dashboard.Dashboard.selection
        |> HttpUtil.mutationWith expect authorizedAccess
