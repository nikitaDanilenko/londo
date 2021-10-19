module Pages.Project.NewProject exposing (..)

import Configuration exposing (Configuration)
import LondoGQL.InputObject exposing (AccessorsInput, ProjectCreation)



type alias Model =
    { token : String
    , configuration : Configuration
    , projectCreation : ProjectCreation
    }


type Msg
    = SetName String
    | SetDescription String
    | SetFlatIfSingleTask Bool
    | SetReadAccessors AccessorsInput
    | SetWriteAccessors AccessorsInput
    | Create
    | Cancel

