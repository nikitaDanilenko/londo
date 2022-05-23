module Pages.Project.ProjectEditor exposing (Model, Msg, init, view, update)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Either exposing (Either(..))
import Graphql.OptionalArgument as OptionalArgument
import Graphql.SelectionSet as SelectionSet
import Html exposing (Html, button, div, input, label, td, text, thead, tr)
import Html.Attributes exposing (checked, class, disabled, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Language.Language as Language exposing (Language)
import List.Extra
import LondoGQL.InputObject exposing (ProjectCreation, ProjectUpdate)
import LondoGQL.Mutation as Mutation
import LondoGQL.Object.Project
import LondoGQL.Object.ProjectId
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
import Util.LensUtil as LensUtil


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
                            (projectIdIs projectId |> LensUtil.firstSuch)
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
            case graphQLDataOrError of
                Success project ->
                    ( model
                        |> Optional.modify
                            (ownProjectsLens
                                |> Compose.lensWithOptional (projectIdIs projectId |> LensUtil.firstSuch)
                            )
                            (Either.andThenRight (always (Left project)))
                    , Cmd.none
                    )

                -- todo: Handle error case
                _ ->
                    ( model, Cmd.none )

        EnterEditProjectAt projectId ->
            ( model
                |> Optional.modify (ownProjectsLens |> Compose.lensWithOptional (projectIdIs projectId |> LensUtil.firstSuch))
                    (Either.unpack (\project -> { original = project, update = ProjectUpdateClientInput.from project }) identity >> Right)
            , Cmd.none
            )

        ExitEditProjectAt projectId ->
            ( model |> Optional.modify (ownProjectsLens |> Compose.lensWithOptional (projectIdIs projectId |> LensUtil.firstSuch)) (Either.unpack identity .original >> Left), Cmd.none )

        DeleteProject projectId ->
            ( model
            , deleteProject model projectId
            )

        GotDeleteProjectResponse graphQLDataOrError ->
            case graphQLDataOrError of
                Success deletedId ->
                    ( model
                        |> ownProjectsLens.set
                            (model.ownProjects
                                |> List.Extra.filterNot
                                    (Either.unpack
                                        (\t -> t.id == deletedId)
                                        (\t -> t.original.id == deletedId)
                                    )
                            )
                    , Cmd.none
                    )

                -- todo: Handle error case
                _ ->
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


view : Model -> Html Msg
view model =
    let
        viewEditProjects =
            List.map
                (Either.unpack
                    editOrDeleteProjectLine
                    (\e -> e.update |> editProjectLine model.language e.original.id)
                )
    in
    div [ id "addProjectView" ]
        (div [ id "addProject" ]
            [ label [ for "projectName" ] [ text model.language.newProject ] ]
            :: div [ id "addProject" ] [ button [ class "button", onClick CreateProject ] [ text model.language.newProject ] ]
            :: thead []
                [ tr []
                    [ td [] [ label [] [ text model.language.name ] ]
                    , td [] [ label [] [ text model.language.description ] ]
                    , td [] [ label [] [ text model.language.flatIfSingleTask ] ]
                    ]
                ]
            :: viewEditProjects model.ownProjects
        )


editOrDeleteProjectLine : ProjectInformation -> Html Msg
editOrDeleteProjectLine projectInformation =
    tr [ id "editingProject" ]
        [ td [] [ label [] [ text projectInformation.name ] ]
        , td [] [ label [] [ projectInformation.description |> Maybe.withDefault "" |> text ] ]
        , td []
            [ input
                [ type_ "checkbox"
                , checked projectInformation.flatIfSingleTask
                , disabled True
                ]
                []
            ]
        , td [] [ label [] [ text projectInformation.name ] ]
        ]


editProjectLine : Language.ProjectEditor -> ProjectId -> ProjectUpdateClientInput -> Html Msg
editProjectLine language projectId projectUpdateClientInput =
    let
        createOnEnter =
            onEnter (SaveProjectEdit projectId)
    in
    -- todo: Check whether the update behaviour is correct. There is the implicit assumption that the update originates from the project.
    --       cf. name, description, and flatIfSingleTask
    div [ class "plainTaskLine" ]
        [ div [ class "plainName" ]
            [ label [] [ text language.name ]
            , input
                [ value projectUpdateClientInput.name
                , onInput (flip ProjectUpdateClientInput.name.set projectUpdateClientInput >> UpdateProject projectId)
                , createOnEnter
                ]
                []
            ]
        , div [ class "projectDescriptionArea" ]
            [ label [] [ text language.description ]
            , div [ class "projectDescription" ]
                [ input
                    [ Maybe.withDefault "" projectUpdateClientInput.description |> value
                    , onInput
                        (flip
                            (Just
                                >> Maybe.Extra.filter (String.isEmpty >> not)
                                >> ProjectUpdateClientInput.description.set
                            )
                            projectUpdateClientInput
                            >> UpdateProject projectId
                        )
                    , createOnEnter
                    ]
                    []
                ]
            ]
        , div [ id "flatIfSingleTaskArea" ]
            [ label [ for "flatIfSingleTask" ] [ text language.flatIfSingleTask ]
            , input
                [ type_ "checkbox"
                , checked projectUpdateClientInput.flatIfSingleTask
                , onClick
                    (ProjectUpdateClientInput.flatIfSingleTask.set (not projectUpdateClientInput.flatIfSingleTask)
                        projectUpdateClientInput
                        |> UpdateProject projectId
                    )
                ]
                []
            ]
        , button [ class "button", onClick (SaveProjectEdit projectId) ]
            [ text language.save ]
        , button [ class "button", onClick (ExitEditProjectAt projectId) ]
            [ text language.cancel ]
        ]


projectIdIs : ProjectId -> Either ProjectInformation (Editing ProjectInformation ProjectUpdateClientInput) -> Bool
projectIdIs projectId =
    Either.unpack
        (\p -> p.id == projectId)
        (\e -> e.original.id == projectId)


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
        { projectId = projectId |> ProjectId.toInput
        , projectUpdate = projectUpdate
        }
        ProjectInformation.selection
        |> RequestUtil.mutateWith (graphQLRequestParametersOf model (GotSaveProjectResponse projectId))


deleteProject : Model -> ProjectId -> Cmd Msg
deleteProject model projectId =
    Mutation.deleteProject
        { projectId = projectId |> ProjectId.toInput
        }
        (SelectionSet.map ProjectId (LondoGQL.Object.Project.id LondoGQL.Object.ProjectId.uuid))
        |> RequestUtil.mutateWith (graphQLRequestParametersOf model GotDeleteProjectResponse)
