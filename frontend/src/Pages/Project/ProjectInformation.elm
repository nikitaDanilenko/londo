module Pages.Project.ProjectInformation exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import List.Nonempty
import LondoGQL.Object
import LondoGQL.Object.Accessors
import LondoGQL.Object.NonEmptyList
import LondoGQL.Object.Project
import LondoGQL.Object.ProjectId
import LondoGQL.Object.UserId
import Types.Accessors as Accessors exposing (Accessors)
import Types.ProjectId exposing (ProjectId)
import Types.UserId exposing (UserId)


type alias ProjectInformation =
    { id : ProjectId
    , name : String
    , description : Maybe String
    , ownerId : UserId
    , flatIfSingleTask : Bool
    , readAccessors : Accessors
    , writeAccessors : Accessors
    }


selection : SelectionSet ProjectInformation LondoGQL.Object.Project
selection =
    SelectionSet.map7 ProjectInformation
        (LondoGQL.Object.Project.id LondoGQL.Object.ProjectId.uuid |> SelectionSet.map ProjectId)
        LondoGQL.Object.Project.name
        LondoGQL.Object.Project.description
        (LondoGQL.Object.Project.ownerId LondoGQL.Object.UserId.uuid |> SelectionSet.map UserId)
        LondoGQL.Object.Project.flatIfSingleTask
        (SelectionSet.map2 (\isAllowList userIds -> Accessors.from { isAllowList = isAllowList, userIds = userIds })
            (LondoGQL.Object.Project.readAccessors LondoGQL.Object.Accessors.isAllowList)
            (LondoGQL.Object.Project.readAccessors userIdsSelection)
        )
        (SelectionSet.map2 (\isAllowList userIds -> Accessors.from { isAllowList = isAllowList, userIds = userIds })
            (LondoGQL.Object.Project.writeAccessors LondoGQL.Object.Accessors.isAllowList)
            (LondoGQL.Object.Project.writeAccessors userIdsSelection)
        )


userIdsSelection : SelectionSet (Maybe (List.Nonempty.Nonempty UserId)) LondoGQL.Object.Accessors
userIdsSelection =
    LondoGQL.Object.Accessors.userIds
        (SelectionSet.map2 List.Nonempty.Nonempty
            (LondoGQL.Object.NonEmptyList.head LondoGQL.Object.UserId.uuid |> SelectionSet.map UserId)
            (LondoGQL.Object.NonEmptyList.tail LondoGQL.Object.UserId.uuid |> SelectionSet.map (List.map UserId))
        )
