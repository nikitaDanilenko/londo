module Types.Task.Resolved exposing (..)

import Types.Task.Task exposing (Task)


type alias ResolvedTask =
    { task : Task
    , simulation : Maybe Simulation
    }
