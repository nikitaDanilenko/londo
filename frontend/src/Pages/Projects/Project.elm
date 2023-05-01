module Pages.Projects.Project exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.Project
import LondoGQL.Object.ProjectId
import Monocle.Lens exposing (Lens)
import Types.ProjectId exposing (ProjectId)


type alias Project =
    { projectId : ProjectId
    , name : String
    , description : Maybe String
    }


lenses :
    { name : Lens Project String
    , description : Lens Project (Maybe String)
    }
lenses =
    { name = Lens .name (\b a -> { a | name = b })
    , description = Lens .description (\b a -> { a | description = b })
    }


selection : SelectionSet Project LondoGQL.Object.Project
selection =
    SelectionSet.map3 Project
        (LondoGQL.Object.Project.id LondoGQL.Object.ProjectId.uuid |> SelectionSet.map ProjectId)
        LondoGQL.Object.Project.name
        LondoGQL.Object.Project.description
