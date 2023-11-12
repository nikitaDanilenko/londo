module Pages.Statistics.EditingResolvedProject exposing (..)

import Monocle.Lens exposing (Lens)
import Types.Project.Project
import Types.Task.Analysis
import Types.Task.Id
import Types.Task.TaskWithSimulation
import Util.DictList as DictList exposing (DictList)
import Util.Editing exposing (Editing)


type alias EditingResolvedProject =
    { project : Types.Project.Project.Project
    , tasks : DictList Types.Task.Id.Id (Editing Types.Task.Analysis.Analysis Types.Task.TaskWithSimulation.ClientInput)
    }


tasks : EditingResolvedProject -> List Types.Task.Analysis.Analysis
tasks =
    .tasks
        >> DictList.values
        >> List.map .original


lenses :
    { project : Lens EditingResolvedProject Types.Project.Project.Project
    , tasks : Lens EditingResolvedProject (DictList Types.Task.Id.Id (Editing Types.Task.Analysis.Analysis Types.Task.TaskWithSimulation.ClientInput))
    }
lenses =
    { project = Lens .project (\b a -> { a | project = b })
    , tasks = Lens .tasks (\b a -> { a | tasks = b })
    }
