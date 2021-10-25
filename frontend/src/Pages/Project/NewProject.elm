module Pages.Project.NewProject exposing (..)

import Bootstrap.Button as Button
import Bootstrap.ButtonGroup as ButtonGroup
import Configuration exposing (Configuration)
import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import GraphQLFunctions.ProjectCreationUtil as ProjectCreationUtil
import Graphql.Http
import Graphql.OptionalArgument as OptionalArgument
import Html exposing (Html, button, div, input, label, text)
import Html.Attributes exposing (checked, class, disabled, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Language.Language as Language exposing (Language)
import LondoGQL.InputObject exposing (AccessorsInput, ProjectCreation)
import LondoGQL.Mutation as Mutation
import LondoGQL.Object exposing (ProjectId)
import LondoGQL.Object.Project
import LondoGQL.Object.ProjectId
import LondoGQL.Scalar exposing (Uuid)
import Maybe.Extra
import Monocle.Compose as Compose
import Monocle.Lens exposing (Lens)
import RemoteData exposing (RemoteData)


type alias Model =
    { token : String
    , configuration : Configuration
    , language : Language.NewProject
    , projectCreation : ProjectCreation
    }


type Msg
    = SetName String
    | SetDescription String
    | SetFlatIfSingleTask
    | SetReadAccessors AccessorsInput
    | SetWriteAccessors AccessorsInput
    | Create
    | GotResponse (RemoteData (Graphql.Http.Error Uuid) Uuid)


projectCreationLens : Lens Model ProjectCreation
projectCreationLens =
    Lens .projectCreation (\b a -> { a | projectCreation = b })


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
            , language = flags.language.newProject
            , projectCreation =
                { name = ""
                , description = Nothing |> OptionalArgument.fromMaybe
                , flatIfSingleTask = True
                , readAccessors = { isAllowList = False, userIds = Nothing |> OptionalArgument.fromMaybe }
                , writeAccessors = { isAllowList = False, userIds = Nothing |> OptionalArgument.fromMaybe }
                }
            }
    in
    ( model, Cmd.none )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        SetName name ->
            ( model |> (projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.name).set name, Cmd.none )

        SetDescription description ->
            ( model |> (projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.description).set (Just description), Cmd.none )

        SetFlatIfSingleTask ->
            let
                lens =
                    projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.flatIfSingleTask
            in
            ( model |> lens.set (model |> lens.get |> not), Cmd.none )

        SetReadAccessors readAccessors ->
            ( model |> (projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.readAccessors).set readAccessors, Cmd.none )

        SetWriteAccessors writeAccessors ->
            ( model |> (projectCreationLens |> Compose.lensWithLens ProjectCreationUtil.writeAccessors).set writeAccessors, Cmd.none )

        Create ->
            ( model, create model )

        -- todo: Redirect to correct frame (edit? overview?)
        GotResponse remoteData ->
            ( model, Cmd.none )


view : Model -> Html Msg
view model =
    let
        createOnEnter =
            onEnter Create
    in
    div [ id "creatingProjectView" ]
        [ div [ id "creatingProject" ]
            [ label [ for "projectName" ] [ text model.language.name ]
            , input
                [ onInput SetName
                , value model.projectCreation.name
                , createOnEnter
                ]
                []
            ]
        , div [ id "creatingDescription" ]
            [ label [ for "projectDescription" ] [ text model.language.description ]
            , input
                [ onInput SetDescription
                , value (model.projectCreation.description |> OptionalArgumentUtil.toMaybe |> Maybe.withDefault "")
                , createOnEnter
                ]
                []
            ]
        , div [ id "creatingFlatIfSingleTask" ]
            [ label [ for "flatIfSingleTask" ] [ text model.language.flatIfSingleTask ]
            , input
                [ type_ "checkbox"
                , checked model.projectCreation.flatIfSingleTask
                , onClick SetFlatIfSingleTask
                ]
                []
            ]
        , div [ id "creatingReadableBy" ]
            [ label [ for "readableBy" ] [ text model.language.readableBy ]
            , viewAccessors model.language model.projectCreation.readAccessors
            ]
        , div [ id "creatingWritableBy" ]
            [ label [ for "writableBy" ] [ text model.language.writableBy ]
            , viewAccessors model.language model.projectCreation.writeAccessors
            ]
        , button
            [ class "button"
            , onClick Create
            , disabled (not (isValidProjectCreation model.projectCreation))
            ]
            [ text model.language.create ]
        ]


isValidProjectCreation : ProjectCreation -> Bool
isValidProjectCreation =
    .name >> String.isEmpty >> not


viewAccessors : Language.NewProject -> AccessorsInput -> Html Msg
viewAccessors language accessors =
    let
        usersOption =
            OptionalArgumentUtil.toMaybe accessors.userIds

        usersDefined =
            Maybe.Extra.isJust usersOption
    in
    ButtonGroup.radioButtonGroup
        []
        -- todo: The assignment via the radio buttons seems wrong
        [ ButtonGroup.radioButton (accessors.isAllowList && not usersDefined)
            [ Button.primary, Button.onClick (SetReadAccessors accessors) ]
            [ text language.nobody ]
        , ButtonGroup.radioButton (accessors.isAllowList && usersDefined)
            [ Button.primary, Button.onClick (SetReadAccessors accessors) ]
            [ text language.onlyUsers ]
        , ButtonGroup.radioButton (not accessors.isAllowList && not usersDefined)
            [ Button.primary, Button.onClick (SetReadAccessors accessors) ]
            [ text language.everybody ]
        , ButtonGroup.radioButton (not accessors.isAllowList && usersDefined)
            [ Button.primary, Button.onClick (SetReadAccessors accessors) ]
            [ text language.exceptUsers ]
        ]


create : Model -> Cmd Msg
create model =
    Mutation.createProject { projectCreation = model.projectCreation } (LondoGQL.Object.Project.id LondoGQL.Object.ProjectId.uuid)
        |> Graphql.Http.mutationRequest model.configuration.graphQLEndpoint
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)
