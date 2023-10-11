module Pages.Statistics.EditingResolvedProject exposing (..)

import Monocle.Lens exposing (Lens)
import Types.Project.Project
import Types.Task.Id
import Types.Task.Resolved
import Types.Task.Update
import Util.DictList as DictList exposing (DictList)
import Util.Editing exposing (Editing)


type alias EditingResolvedProject =
    { project : Types.Project.Project.Project

    -- Todo: The update should be one that contains simulations as well
    , tasks : DictList Types.Task.Id.Id (Editing Types.Task.Resolved.Resolved Types.Task.Update.ClientInput)
    }


tasks : EditingResolvedProject -> List Types.Task.Resolved.Resolved
tasks =
    .tasks
        >> DictList.values
        >> List.map .original


lenses :
    { project : Lens EditingResolvedProject Types.Project.Project.Project
    , tasks : Lens EditingResolvedProject (DictList Types.Task.Id.Id (Editing Types.Task.Resolved.Resolved Types.Task.Update.ClientInput))
    }
lenses =
    { project = Lens .project (\b a -> { a | project = b })
    , tasks = Lens .tasks (\b a -> { a | tasks = b })
    }
