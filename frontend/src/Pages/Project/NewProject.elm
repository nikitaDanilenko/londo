module Pages.Project.NewProject exposing (..)

import Basics.Extra exposing (flip)
import Bootstrap.Button as Button
import Bootstrap.ButtonGroup as ButtonGroup
import Configuration exposing (Configuration)
import GraphQLFunctions.Lens.PlainCreation as PlainCreation
import GraphQLFunctions.Lens.ProgressInput as ProgressInput
import GraphQLFunctions.Lens.ProjectReferenceCreation as ProjectReferenceCreation
import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import GraphQLFunctions.ProjectCreation as ProjectCreationUtil
import Graphql.Http
import Graphql.OptionalArgument as OptionalArgument
import Html exposing (Html, button, div, input, label, text)
import Html.Attributes exposing (checked, class, disabled, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Language.Language as Language exposing (Language)
import List.Extra
import LondoGQL.Enum.TaskKind as TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (AccessorsInput, PlainCreation, ProgressInput, ProjectCreation, ProjectReferenceCreation)
import LondoGQL.Mutation as Mutation
import LondoGQL.Object.Project
import LondoGQL.Object.ProjectId
import LondoGQL.Scalar exposing (Natural(..), Positive(..), Uuid)
import Maybe.Extra
import Monocle.Common exposing (list)
import Monocle.Compose as Compose
import Monocle.Lens exposing (Lens)
import Pages.Util.AccessorUtil as AccessorsUtil
import Pages.Util.MathUtil as MathUtil
import Pages.Util.ScalarUtil as ScalarUtil
import RemoteData exposing (RemoteData)


type alias Model =
    { token : String
    , configuration : Configuration
    , language : Language.NewProject
    , projectCreation : ProjectCreation
    , plainTasks : List PlainCreation
    , projectReferenceTasks : List ProjectReferenceCreation
    }


type Msg
    = SetName String
    | SetDescription String
    | SetFlatIfSingleTask
    | SetReadAccessors AccessorsInput
    | SetWriteAccessors AccessorsInput
    | AddPlainTask
    | SetPlainTaskAt Int PlainCreation
    | DeletePlainTaskAt Int
    | AddProjectReferenceTask
    | SetProjectReferenceTaskAt Int ProjectReferenceCreation
    | DeleteProjectReferenceTaskAt Int
    | Create
    | GotResponse (RemoteData (Graphql.Http.Error Uuid) Uuid)


projectCreationLens : Lens Model ProjectCreation
projectCreationLens =
    Lens .projectCreation (\b a -> { a | projectCreation = b })


plainTasksLens : Lens Model (List PlainCreation)
plainTasksLens =
    Lens .plainTasks (\b a -> { a | plainTasks = b })


projectReferenceTasksLens : Lens Model (List ProjectReferenceCreation)
projectReferenceTasksLens =
    Lens .projectReferenceTasks (\b a -> { a | projectReferenceTasks = b })


defaultPlainCreation : PlainCreation
defaultPlainCreation =
    { name = ""
    , taskKind = TaskKind.Fractional
    , unit = OptionalArgument.Absent
    , progress =
        { reachable = Positive "1"
        , reached = Natural "0"
        }
    , weight = Positive "1"
    }


defaultProjectReferenceCreation : ProjectReferenceCreation
defaultProjectReferenceCreation =
    { weight = Positive "1"
    , projectReferenceId =
        { uuid = Uuid ""
        }
    }


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
                , readAccessors = AccessorsUtil.everybody
                , writeAccessors = AccessorsUtil.nobody
                }
            , plainTasks = []
            , projectReferenceTasks = []
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

        AddPlainTask ->
            ( model |> plainTasksLens.set (defaultPlainCreation :: model.plainTasks), Cmd.none )

        SetPlainTaskAt pos plainCreation ->
            ( model
                |> (plainTasksLens |> Compose.lensWithOptional (list pos)).set
                    plainCreation
            , Cmd.none
            )

        DeletePlainTaskAt pos ->
            ( model
                |> plainTasksLens.set
                    (model.plainTasks
                        |> List.Extra.removeAt pos
                    )
            , Cmd.none
            )

        AddProjectReferenceTask ->
            ( model |> projectReferenceTasksLens.set (defaultProjectReferenceCreation :: model.projectReferenceTasks), Cmd.none )

        SetProjectReferenceTaskAt int projectReferenceCreation ->
            ( model
                |> (projectReferenceTasksLens |> Compose.lensWithOptional (list int)).set
                    projectReferenceCreation
            , Cmd.none
            )

        DeleteProjectReferenceTaskAt pos ->
            ( model
                |> projectReferenceTasksLens.set
                    (model.projectReferenceTasks
                        |> List.Extra.removeAt pos
                    )
            , Cmd.none
            )


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
            , viewAccessors model.language SetReadAccessors model.projectCreation.readAccessors
            ]
        , div [ id "creatingWritableBy" ]
            [ label [ for "writableBy" ] [ text model.language.writableBy ]
            , viewAccessors model.language SetWriteAccessors model.projectCreation.writeAccessors
            ]
        , div [ id "creatingPlainTasks" ]
            (button [ class "button", onClick AddPlainTask ] [ text model.language.newPlainTask ]
                :: List.indexedMap (editPlainTaskLine model.language) model.plainTasks
            )
        , div [ id "creatingProjectReferenceTasks" ]
            (button [ class "button", onClick AddProjectReferenceTask ] [ text model.language.newProjectReferenceTask ]
                :: List.indexedMap (editProjectReferenceTaskLine model.language) model.projectReferenceTasks
            )
        , button
            [ class "button"
            , onClick Create
            , disabled (not (isValidProjectCreation model.projectCreation))
            ]
            [ text model.language.create ]
        ]


editPlainTaskLine : Language.NewProject -> Int -> PlainCreation -> Html Msg
editPlainTaskLine language pos plainCreation =
    let
        taskKindRadioButton : TaskKind -> String -> ButtonGroup.RadioButtonItem Msg
        taskKindRadioButton taskKind description =
            ButtonGroup.radioButton (plainCreation.taskKind == taskKind)
                [ Button.primary
                , Button.onClick (PlainCreation.taskKind.set taskKind plainCreation |> SetPlainTaskAt pos)
                ]
                [ text description ]

        percentualFromDecimal : String -> ProgressInput
        percentualFromDecimal string =
            { reached =
                string
                    |> String.filter Char.isDigit
                    |> Natural
            , reachable =
                100
                    * (1 + MathUtil.numberOfDecimalPlaces string)
                    |> String.fromInt
                    |> Positive
            }

        viewProgress : TaskKind -> List (Html Msg)
        viewProgress taskKind =
            case taskKind of
                TaskKind.Discrete ->
                    let
                        reachableString =
                            ScalarUtil.positiveToString plainCreation.progress.reachable

                        completed =
                            reachableString == ScalarUtil.naturalToString plainCreation.progress.reached

                        complement =
                            if completed then
                                Natural "0"

                            else
                                Natural reachableString
                    in
                    [ input
                        [ type_ "checkbox"
                        , checked completed
                        , onClick
                            (plainCreation
                                |> (PlainCreation.progress |> Compose.lensWithLens ProgressInput.reached).set complement
                                |> SetPlainTaskAt pos
                            )
                        ]
                        []
                    ]

                TaskKind.Percentual ->
                    [ input
                        [ type_ "number"
                        , Html.Attributes.min "0"
                        , onInput
                            (percentualFromDecimal
                                >> flip PlainCreation.progress.set plainCreation
                                >> SetPlainTaskAt pos
                            )
                        ]
                        []
                    ]

                TaskKind.Fractional ->
                    []
    in
    div [ class "plainTaskLine" ]
        [ div [ class "plainName" ]
            [ label [] [ text language.plainTaskName ]
            , input
                [ value plainCreation.name
                , onInput (flip PlainCreation.name.set plainCreation >> SetPlainTaskAt pos)
                ]
                []
            ]
        , div [ class "plainTaskKind" ]
            [ ButtonGroup.radioButtonGroup []
                [ taskKindRadioButton TaskKind.Discrete language.discrete
                , taskKindRadioButton TaskKind.Percentual language.percentual
                , taskKindRadioButton TaskKind.Fractional language.fractional
                ]
            ]
        , div [ class "plainProgress" ] []
        , div [ class "plainUnit" ] []
        , div [ class "weight" ] []
        ]


editProjectReferenceTaskLine : Language.NewProject -> Int -> ProjectReferenceCreation -> Html Msg
editProjectReferenceTaskLine language pos projectReferenceCreation =
    div [ class "projectReferenceLine" ]
        [ div [ class "projectReferenceId" ]
            [ label []
                [ text language.projectReference ]
            , input
                [ projectReferenceCreation.projectReferenceId.uuid |> ScalarUtil.uuidToString |> value
                , onInput
                    (Uuid
                        >> flip ProjectReferenceCreation.projectReferenceId.set projectReferenceCreation
                        >> SetProjectReferenceTaskAt pos
                    )
                ]
                []
            ]
        , div [ class "weight" ]
            [ input
                [ projectReferenceCreation.weight |> ScalarUtil.positiveToString |> value
                , Html.Attributes.min "1"
                , onInput
                    (Positive
                        >> flip ProjectReferenceCreation.weight.set projectReferenceCreation
                        >> SetProjectReferenceTaskAt pos
                    )
                ]
                []
            ]
        , button [ class "button", onClick (DeleteProjectReferenceTaskAt pos) ]
            [ text language.remove ]
        ]


isValidProjectCreation : ProjectCreation -> Bool
isValidProjectCreation =
    .name >> String.isEmpty >> not


viewAccessors : Language.NewProject -> (AccessorsInput -> Msg) -> AccessorsInput -> Html Msg
viewAccessors language setAccessorInput accessors =
    let
        usersOption =
            OptionalArgumentUtil.toMaybe accessors.userIds

        usersDefined =
            Maybe.Extra.isJust usersOption
    in
    ButtonGroup.radioButtonGroup
        []
        [ ButtonGroup.radioButton (accessors.isAllowList && not usersDefined)
            [ Button.primary, Button.onClick (setAccessorInput AccessorsUtil.nobody) ]
            [ text language.nobody ]

        -- todo: The assignment is wrong, since the original value is taken - use proper controls to select values
        --, ButtonGroup.radioButton (accessors.isAllowList && usersDefined)
        --    [ Button.primary, Button.onClick (setAccessorInput { isAllowList = True, userIds = OptionalArgument.fromMaybe usersOption }) ]
        --    [ text language.onlyUsers ]
        , ButtonGroup.radioButton (not accessors.isAllowList && not usersDefined)
            [ Button.primary, Button.onClick (setAccessorInput AccessorsUtil.everybody) ]
            [ text language.everybody ]

        -- todo: The assignment is wrong, since the original value is taken - use proper controls to select values
        --, ButtonGroup.radioButton (not accessors.isAllowList && usersDefined)
        --    [ Button.primary, Button.onClick (setAccessorInput { isAllowList = False, userIds = OptionalArgument.fromMaybe usersOption }) ]
        --    [ text language.exceptUsers ]
        ]


create : Model -> Cmd Msg
create model =
    Mutation.createProject { projectCreation = model.projectCreation } (LondoGQL.Object.Project.id LondoGQL.Object.ProjectId.uuid)
        |> Graphql.Http.mutationRequest model.configuration.graphQLEndpoint
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)
