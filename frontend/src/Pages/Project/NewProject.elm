module Pages.Project.NewProject exposing (..)

import Configuration exposing (Configuration)
import GraphQLFunctions.ProjectCreationUtil as ProjectCreationUtil
import Graphql.Http
import LondoGQL.InputObject exposing (AccessorsInput, ProjectCreation)
import LondoGQL.Mutation as Mutation
import LondoGQL.Object exposing (ProjectId)
import LondoGQL.Object.Project
import LondoGQL.Object.ProjectId
import LondoGQL.Scalar exposing (Uuid)
import Monocle.Compose as Compose
import Monocle.Lens exposing (Lens)
import RemoteData exposing (RemoteData)
import UUID exposing (UUID)


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
    | GotResponse (RemoteData (Graphql.Http.Error Uuid) Uuid)


projectCreationLens : Lens Model ProjectCreation
projectCreationLens =
    Lens .projectCreation (\b a -> { a | projectCreation = b })

update : Msg -> Model -> (Model, Cmd Msg)
update msg model = case msg of
    SetName name ->
        (model |> (projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.name).set name, Cmd.none)

    SetDescription description ->
        (model |> (projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.description).set (Just description), Cmd.none)

    SetFlatIfSingleTask flatIfSingleTask ->
        (model |> (projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.flatIfSingleTask).set flatIfSingleTask, Cmd.none)

    SetReadAccessors readAccessors ->
        (model |> (projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.readAccessors).set readAccessors, Cmd.none)

    SetWriteAccessors writeAccessors ->
        (model |> (projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.writeAccessors).set writeAccessors, Cmd.none)

    Create ->
        (model, create model)

-- todo: Redirect to correct frame (edit? overview?)
    GotResponse remoteData ->
        (model, Cmd.none)


create : Model -> Cmd Msg
create model =
    Mutation.createProject  {projectCreation = model.projectCreation} (LondoGQL.Object.Project.id LondoGQL.Object.ProjectId.uuid)
    |> Graphql.Http.mutationRequest model.configuration.graphQLEndpoint
    |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)