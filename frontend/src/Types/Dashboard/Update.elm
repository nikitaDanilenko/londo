module Types.Dashboard.Update exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.Enum.Visibility
import LondoGQL.InputObject
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Dashboard.Dashboard
import Types.Dashboard.Id
import Util.HttpUtil as HttpUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { header : ValidatedInput String
    , description : Maybe String
    , visibility : LondoGQL.Enum.Visibility.Visibility
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


from : Types.Dashboard.Dashboard.Dashboard -> ClientInput
from dashboard =
    { header =
        ValidatedInput.nonEmptyString
            |> ValidatedInput.lenses.value.set dashboard.header
            |> ValidatedInput.lenses.text.set dashboard.header
    , description = dashboard.description
    , visibility = dashboard.visibility
    }


toGraphQLInput : Types.Dashboard.Id.Id -> ClientInput -> LondoGQL.InputObject.UpdateDashboardInput
toGraphQLInput dashboardId input =
    { dashboardId = dashboardId |> Types.Dashboard.Id.toGraphQLInput
    , dashboardUpdate =
        { header = input.header.value
        , description = input.description |> OptionalArgument.fromMaybe
        , visibility = input.visibility
        }
    }


updateWith :
    (HttpUtil.GraphQLResult Types.Dashboard.Dashboard.Dashboard -> msg)
    -> AuthorizedAccess
    -> Types.Dashboard.Id.Id
    -> ClientInput
    -> Cmd msg
updateWith expect authorizedAccess dashboardId update =
    LondoGQL.Mutation.updateDashboard
        { input = update |> toGraphQLInput dashboardId }
        Types.Dashboard.Dashboard.selection
        |> HttpUtil.mutationWith expect authorizedAccess
