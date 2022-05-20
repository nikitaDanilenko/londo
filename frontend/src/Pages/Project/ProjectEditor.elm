module Pages.Project.ProjectEditor exposing (..)

import Configuration exposing (Configuration)
import Either exposing (Either(..))
import Graphql.OptionalArgument as OptionalArgument
import Language.Language as Language exposing (Language)
import List.Extra
import LondoGQL.InputObject exposing (ProjectCreation, ProjectUpdate)
import LondoGQL.Mutation as Mutation
import LondoGQL.Query as Query
import Maybe.Extra
import Monocle.Compose as Compose
import Monocle.Lens as Lens exposing (Lens)
import Monocle.Optional as Optional exposing (Optional)
import Pages.Project.ProjectInformation as ProjectInformation exposing (ProjectInformation)
import Pages.Project.ProjectUpdateClientInput as ProjectUpdateClientInput exposing (ProjectUpdateClientInput)
import Pages.Util.AccessorUtil as AccessorsUtil
import Pages.Util.RequestUtil as RequestUtil
import RemoteData exposing (RemoteData(..))
import Types.ProjectId as ProjectId exposing (ProjectId)
import Util.Editing as Editing exposing (Editing)


type alias Model =
    { token : String
    , configuration : Configuration
    , language : Language.ProjectEditor
    , ownProjects : List (Either ProjectInformation (Editing ProjectInformation ProjectUpdateClientInput))
    , writeAccessProjects : List (Either ProjectInformation (Editing ProjectInformation ProjectUpdateClientInput))
    }


ownProjectsLens : Lens Model (List (Either ProjectInformation (Editing ProjectInformation ProjectUpdateClientInput)))
ownProjectsLens =
    Lens .ownProjects (\b a -> { a | ownProjects = b })


writeAccessProjectsLens : Lens Model (List (Either ProjectInformation (Editing ProjectInformation ProjectUpdateClientInput)))
writeAccessProjectsLens =
    Lens .writeAccessProjects (\b a -> { a | writeAccessProjects = b })


type Msg
    = CreateProject
    | GotCreateProjectResponse (RequestUtil.GraphQLDataOrError ProjectInformation)
    | UpdateProject ProjectId ProjectUpdateClientInput
    | SaveProjectEdit ProjectId
    | GotSaveProjectResponse ProjectId (RequestUtil.GraphQLDataOrError ProjectInformation)
    | EnterEditProjectAt ProjectId
    | ExitEditProjectAt ProjectId
    | DeleteProject ProjectId
    | GotDeleteProjectResponse (RequestUtil.GraphQLDataOrError ProjectId)
    | GotFetchOwnProjectsResponse (RequestUtil.GraphQLDataOrError (List ProjectInformation))
    | GotFetchWriteAccessProjectsResponse (RequestUtil.GraphQLDataOrError (List ProjectInformation))


type alias Flags =
    { token : String
    , configuration : Configuration
    , language : Language
    }


init : Flags -> ( Model, Cmd Msg )
init flags =
    let
        model =
            { token = flags.token
            , configuration = flags.configuration
            , language = flags.language.projectEditor
            , ownProjects = []
            , writeAccessProjects = []
            }
    in
    ( model, Cmd.batch [ fetchOwnProjects model, fetchWriteAccessProjects model ] )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        CreateProject ->
            ( model, createProject model )

        GotCreateProjectResponse graphQLDataOrError ->
            case graphQLDataOrError of
                Success projectInformation ->
                    let
                        newModel =
                            Lens.modify ownProjectsLens
                                (\ts ->
                                    Right
                                        { original = projectInformation
                                        , update = ProjectUpdateClientInput.from projectInformation
                                        }
                                        :: ts
                                )
                                model
                    in
                    ( newModel, Cmd.none )

                _ ->
                    -- todo: Handle error case
                    ( model, Cmd.none )

        UpdateProject projectId projectUpdateClientInput ->
            ( model
                |> Optional.modify
                    (ownProjectsLens
                        |> Compose.lensWithOptional
                            (projectIdIs projectId |> firstSuch)
                    )
                    (Either.mapRight (Editing.updateLens.set projectUpdateClientInput))
            , Cmd.none
            )

        SaveProjectEdit projectId ->
            let
                cmd =
                    Maybe.Extra.unwrap
                        Cmd.none
                        (Either.unwrap Cmd.none
                            (\editing ->
                                saveProject model
                                    (ProjectUpdateClientInput.to projectId editing.original.ownerId editing.update |> ProjectInformation.toUpdate)
                                    editing.original.id
                            )
                        )
                        (List.Extra.find (projectIdIs projectId) model.ownProjects)
            in
            ( model, cmd )

        GotSaveProjectResponse projectId graphQLDataOrError ->
            ( model, Cmd.none )

        EnterEditProjectAt projectId ->
            ( model, Cmd.none )

        ExitEditProjectAt projectId ->
            ( model, Cmd.none )

        DeleteProject projectId ->
            ( model, Cmd.none )

        GotDeleteProjectResponse graphQLDataOrError ->
            ( model, Cmd.none )

        GotFetchOwnProjectsResponse graphQLDataOrError ->
            case graphQLDataOrError of
                Success ownProjects ->
                    ( model |> ownProjectsLens.set (ownProjects |> List.map Left), Cmd.none )

                -- todo: Handle error case
                _ ->
                    ( model, Cmd.none )

        GotFetchWriteAccessProjectsResponse graphQLDataOrError ->
            case graphQLDataOrError of
                Success writeAccessProjects ->
                    ( model |> writeAccessProjectsLens.set (writeAccessProjects |> List.map Left), Cmd.none )

                -- todo: Handle error case
                _ ->
                    ( model, Cmd.none )


firstSuch : (a -> Bool) -> Optional (List a) a
firstSuch p =
    { getOption = List.Extra.find p
    , set = List.Extra.setIf p
    }


projectIdIs : ProjectId -> Either ProjectInformation (Editing ProjectInformation ProjectUpdateClientInput) -> Bool
projectIdIs projectId =
    Either.unpack (\p -> p.id == projectId)
        (\p -> p.original.id == projectId)


fetchOwnProjects : Model -> Cmd Msg
fetchOwnProjects model =
    Query.fetchOwn
        ProjectInformation.selection
        |> RequestUtil.queryWith (graphQLRequestParametersOf model GotFetchOwnProjectsResponse)


fetchWriteAccessProjects : Model -> Cmd Msg
fetchWriteAccessProjects model =
    Query.fetchWithWriteAccess
        ProjectInformation.selection
        |> RequestUtil.queryWith (graphQLRequestParametersOf model GotFetchWriteAccessProjectsResponse)


graphQLRequestParametersOf : Model -> (RequestUtil.GraphQLDataOrError a -> Msg) -> RequestUtil.GraphQLRequestParameters a Msg
graphQLRequestParametersOf model gotResponse =
    { endpoint = model.configuration.graphQLEndpoint
    , token = model.token
    , gotResponse = gotResponse
    }


defaultProjectCreation : ProjectCreation
defaultProjectCreation =
    { name = ""
    , description = Nothing |> OptionalArgument.fromMaybe
    , flatIfSingleTask = True
    , readAccessors = AccessorsUtil.nobody
    , writeAccessors = AccessorsUtil.nobody
    }


createProject : Model -> Cmd Msg
createProject model =
    Mutation.createProject
        { projectCreation = defaultProjectCreation
        }
        ProjectInformation.selection
        |> RequestUtil.mutateWith (graphQLRequestParametersOf model GotCreateProjectResponse)


saveProject : Model -> ProjectUpdate -> ProjectId -> Cmd Msg
saveProject model projectUpdate projectId =
    Mutation.updateProject
        { projectId = projectId |> ProjectId.uuid |> LondoGQL.InputObject.ProjectIdInput
        , projectUpdate = projectUpdate
        }
        ProjectInformation.selection
        |> RequestUtil.mutateWith (graphQLRequestParametersOf model (GotSaveProjectResponse projectId))
