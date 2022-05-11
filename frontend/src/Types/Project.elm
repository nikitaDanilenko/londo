module Types.Project exposing (..)

import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.InputObject exposing (ProjectUpdate)
import Types.ProjectId exposing (ProjectId)
import Types.UserId as UserId exposing (UserId)


type alias Project =
    { id : ProjectId
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
