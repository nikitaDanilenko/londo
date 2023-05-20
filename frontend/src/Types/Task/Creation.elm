module Types.Task.Creation exposing (..)

import Graphql.Http
import Graphql.OptionalArgument as OptionalArgument
import LondoGQL.Enum.TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (ProgressInput)
import LondoGQL.Mutation
import Monocle.Lens exposing (Lens)
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Types.Progress.Input
import Types.Project.ProjectId
import Types.Task.Task
import Util.HttpUtil as HttpUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { projectId : Types.Project.ProjectId.ProjectId
    , name : ValidatedInput String
    , taskKind : TaskKind
    , unit : Maybe String
    , progress : Types.Progress.Input.ClientInput
    , counting : Bool
    }


default : Types.Project.ProjectId.ProjectId -> ClientInput
default projectId =
    let
        defaultTaskKind =
            LondoGQL.Enum.TaskKind.Percent
    in
    { projectId = projectId
    , name = ValidatedInput.nonEmptyString
    , taskKind = defaultTaskKind
    , unit = Nothing
    , progress = Types.Progress.Input.default defaultTaskKind
    , counting = True
    }


lenses :
    { name : Lens ClientInput (ValidatedInput String)
    , taskKind : Lens ClientInput TaskKind
    , unit : Lens ClientInput (Maybe String)
    , progress : Lens ClientInput Types.Progress.Input.ClientInput
    , counting : Lens ClientInput Bool
    }
lenses =
    { name = Lens .name (\b a -> { a | name = b })
    , taskKind = Lens .taskKind (\b a -> { a | taskKind = b })
    , unit = Lens .unit (\b a -> { a | unit = b })
    , progress = Lens .progress (\b a -> { a | progress = b })
    , counting = Lens .counting (\b a -> { a | counting = b })
    }


toGraphQLInput : ClientInput -> LondoGQL.InputObject.TaskCreation
toGraphQLInput clientInput =
    { name = clientInput.name.value
    , taskKind = clientInput.taskKind
    , unit = clientInput.unit |> OptionalArgument.fromMaybe
    , progress = clientInput.progress |> Types.Progress.Input.toGraphQLInput
    , counting = clientInput.counting
    }


createWith :
    (HttpUtil.GraphQLResult Types.Task.Task.Task -> msg)
    -> AuthorizedAccess
    -> ClientInput
    -> Cmd msg
createWith expect authorizedAccess creation =
    LondoGQL.Mutation.createTask
        { input =
            { projectId = creation |> .projectId |> Types.Project.ProjectId.toGraphQLInput
            , taskCreation = creation |> toGraphQLInput
            }
        }
        Types.Task.Task.selection
        |> Graphql.Http.mutationRequest authorizedAccess.configuration.graphQLEndpoint
        |> HttpUtil.sendWithJWT
            { jwt = authorizedAccess.jwt
            , expect = expect
            }
