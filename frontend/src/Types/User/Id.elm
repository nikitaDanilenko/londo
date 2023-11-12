module Types.User.Id exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.InputObject
import LondoGQL.Object
import LondoGQL.Object.UserId
import LondoGQL.Scalar


type Id
    = Id LondoGQL.Scalar.Uuid


uuid : Id -> LondoGQL.Scalar.Uuid
uuid (Id u) =
    u


toGraphQLInput : Id -> LondoGQL.InputObject.UserIdInput
toGraphQLInput =
    uuid >> LondoGQL.InputObject.UserIdInput


selection : SelectionSet Id LondoGQL.Object.UserId
selection =
    LondoGQL.Object.UserId.uuid |> SelectionSet.map Id
