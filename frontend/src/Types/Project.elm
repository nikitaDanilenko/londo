module Types.Project exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject exposing (ProjectUpdate)
import LondoGQL.Scalar exposing (Uuid)
import Types.UserId as UserId exposing (UserId)


type alias Project =
    { id : Uuid
    , name : String
    , description : Maybe String
    , ownerId : UserId
    , flatIfSingleTask : Bool
    }


toUpdate : Project -> ProjectUpdate
toUpdate p =
    { name = p.name
    , description = OptionalArgument.fromMaybe p.description
    , ownerId = UserId.toInput p.ownerId
    , flatIfSingleTask = p.flatIfSingleTask
    }
