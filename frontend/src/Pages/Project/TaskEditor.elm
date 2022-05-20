module Pages.Project.TaskEditor exposing (..)

import Basics.Extra exposing (flip)
import Bootstrap.Button as Button
import Bootstrap.ButtonGroup as ButtonGroup
import Configuration exposing (Configuration)
import Either exposing (Either(..))
import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import Graphql.OptionalArgument as OptionalArgument
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import Html exposing (Html, button, div, input, label, td, text, thead, tr)
import Html.Attributes exposing (checked, class, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Language.Language as Language exposing (Language)
import List.Extra
import LondoGQL.Enum.TaskKind as TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (PlainCreation, PlainUpdate, ProgressInput)
import LondoGQL.Mutation as Mutation
import LondoGQL.Object
import LondoGQL.Object.Natural
import LondoGQL.Object.Plain
import LondoGQL.Object.Positive
import LondoGQL.Object.Progress
import LondoGQL.Object.Project
import LondoGQL.Object.TaskId
import LondoGQL.Object.UserId
import LondoGQL.Query as Query
import LondoGQL.Scalar exposing (Uuid(..))
import Maybe.Extra
import Monocle.Common exposing (list)
import Monocle.Compose as Compose
import Monocle.Iso exposing (Iso)
import Monocle.Lens as Lens exposing (Lens)
import Monocle.Optional as Optional
import Pages.Project.PlainUpdateClientInput as PlainUpdateClientInput exposing (PlainUpdateClientInput)
import Pages.Project.ProgressClientInput as ProgressClientInput exposing (ProgressClientInput)
import Pages.Util.FromInput as FromInput exposing (FromInput)
import Pages.Util.RequestUtil as RequestUtil
import RemoteData exposing (RemoteData(..))
import Types.Natural as Natural exposing (Natural)
import Types.PlainTask as PlainTask exposing (PlainTask)
import Types.Positive as Positive exposing (Positive)
import Types.Progress as Progress exposing (Progress)
import Types.ProjectId as ProjectId exposing (ProjectId(..))
import Types.TaskId as TaskId exposing (TaskId(..))
import Types.UserId exposing (UserId(..))
import Util.Editing as Editing exposing (Editing)
import Util.LensUtil as LensUtil


type alias Model =
    { token : String
    , configuration : Configuration
    , language : Language.TaskEditor
    , projectInformation : ProjectInformation
    , plainTasks : List (Either PlainTask (Editing PlainTask PlainUpdateClientInput))
    }


type alias ProjectInformation =
    { id : ProjectId
    , name : String
    , description : Maybe String
    , ownerId : UserId
    , flatIfSingleTask : Bool
    }


type Msg
    = AddPlainTask
    | GotAddPlainTaskResponse (RequestUtil.GraphQLDataOrError Uuid)
    | UpdatePlainTask TaskId PlainUpdateClientInput
    | SavePlainTaskEdit TaskId
    | GotSavePlainTaskResponse TaskId (RequestUtil.GraphQLDataOrError PlainTask)
    | EnterEditPlainTaskAt TaskId
    | ExitEditPlainTaskAt TaskId
    | DeletePlainTaskAt TaskId
    | GotDeletePlainTaskResponse (RequestUtil.GraphQLDataOrError TaskId)
    | GotFetchProjectDataResponse (RequestUtil.GraphQLDataOrError ProjectData)


type alias Flags =
    { projectId : ProjectId
    , token : String
    , configuration : Configuration
    , language : Language
    }


type alias ProjectData =
    { projectInformation : ProjectInformation
    , plainTasks : List PlainTask
    }


init : Flags -> ( Model, Cmd Msg )
init flags =
    let
        defaultUuid =
            Uuid "00000000-0000-0000-0000-000000000000"

        model =
            { token = flags.token
            , configuration = flags.configuration
            , language = flags.language.taskEditor
            , projectInformation =
                { id = flags.projectId
                , name = ""
                , description = Nothing
                , ownerId = UserId defaultUuid
                , flatIfSingleTask = False
                }
            , plainTasks = []
            }
    in
    ( model, fetchProjectData model )


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        AddPlainTask ->
            ( model, addPlainTask model )

        GotAddPlainTaskResponse remoteData ->
            case remoteData of
                Success uuid ->
                    let
                        newPlainTask =
                            PlainTask.fromCreation (TaskId uuid) defaultPlainTaskCreation

                        newModel =
                            Lens.modify plainTasksLens
                                (\ts ->
                                    ts
                                        ++ [ Right
                                                { original = newPlainTask
                                                , update = PlainUpdateClientInput.from newPlainTask
                                                }
                                           ]
                                )
                                model
                    in
                    ( newModel, Cmd.none )

                _ ->
                    -- todo: Handle error case
                    ( model, Cmd.none )

        UpdatePlainTask taskId plainUpdateClientInput ->
            ( model
                |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (LensUtil.firstSuch (taskIdIs taskId))) (Either.mapRight (Editing.updateLens.set plainUpdateClientInput))
            , Cmd.none
            )

        SavePlainTaskEdit taskId ->
            let
                cmd =
                    Maybe.Extra.unwrap
                        Cmd.none
                        (Either.unwrap
                            Cmd.none
                            (\editing ->
                                savePlainTask
                                    model
                                    (PlainUpdateClientInput.to editing.update)
                                    editing.original.id
                            )
                        )
                        (List.Extra.find (taskIdIs taskId) model.plainTasks)
            in
            ( model, cmd )

        GotSavePlainTaskResponse taskId remoteData ->
            case remoteData of
                Success plainTask ->
                    ( model
                        |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (LensUtil.firstSuch (taskIdIs taskId))) (Either.andThenRight (always (Left plainTask)))
                    , Cmd.none
                    )

                -- todo: Handle error case
                _ ->
                    ( model, Cmd.none )

        EnterEditPlainTaskAt taskId ->
            ( model
                |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (LensUtil.firstSuch (taskIdIs taskId))) (Either.unpack (\pt -> { original = pt, update = PlainUpdateClientInput.from pt }) identity >> Right)
            , Cmd.none
            )

        ExitEditPlainTaskAt taskId ->
            ( model |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (LensUtil.firstSuch (taskIdIs taskId))) (Either.unpack identity .original >> Left), Cmd.none )

        DeletePlainTaskAt taskId ->
            ( model
            , deletePlainTask model taskId
            )

        GotFetchProjectDataResponse remoteData ->
            case remoteData of
                Success projectData ->
                    ( model
                        |> plainTasksLens.set (projectData.plainTasks |> List.map Left)
                        |> projectInformationLens.set projectData.projectInformation
                    , Cmd.none
                    )

                -- todo: Handle error case
                _ ->
                    ( model, Cmd.none )

        GotDeletePlainTaskResponse remoteData ->
            case remoteData of
                Success deletedId ->
                    ( model
                        |> plainTasksLens.set
                            (model.plainTasks
                                |> List.Extra.filterNot (Either.unpack (\t -> t.id == deletedId) (\t -> t.original.id == deletedId))
                            )
                    , Cmd.none
                    )

                -- todo: Handle error case
                _ ->
                    ( model, Cmd.none )


view : Model -> Html Msg
view model =
    let
        viewEditPlainTasks =
            List.map
                (Either.unpack
                    (editOrDeletePlainTaskLine model.language)
                    (\e -> e.update |> editPlainTaskLine model.language e.original.id)
                )
    in
    div [ id "creatingProjectView" ]
        (div [ id "creatingProject" ]
            [ label [ for "projectName" ] [ text model.language.projectName ]
            , label
                []
                [ text model.projectInformation.name ]
            ]
            :: div [ id "addPlainTask" ] [ button [ class "button", onClick AddPlainTask ] [ text model.language.newPlainTask ] ]
            :: thead []
                [ tr []
                    [ td [] [ label [] [ text model.language.plainTaskName ] ]
                    , td [] [ label [] [ text model.language.taskKind ] ]
                    , td [] [ label [] [ text model.language.progress ] ]
                    , td [] [ label [] [ text model.language.unit ] ]
                    , td [] [ label [] [ text model.language.weight ] ]
                    ]
                ]
            :: viewEditPlainTasks model.plainTasks
        )


percentualIso : Iso ProgressClientInput (FromInput ProgressInput)
percentualIso =
    Iso
        (\pci ->
            FromInput.percentualProgress
                |> FromInput.value.set
                    { reached = pci.reached.value
                    , reachable = pci.reachable.value
                    }
        )
        ProgressClientInput.from


projectInformationLens : Lens Model ProjectInformation
projectInformationLens =
    Lens .projectInformation (\b a -> { a | projectInformation = b })


plainTasksLens : Lens Model (List (Either PlainTask (Editing PlainTask PlainUpdateClientInput)))
plainTasksLens =
    Lens .plainTasks (\b a -> { a | plainTasks = b })


editOrDeletePlainTaskLine : Language.TaskEditor -> PlainTask -> Html Msg
editOrDeletePlainTaskLine language plainTask =
    tr [ id "editingPlainTask" ]
        [ td [] [ label [] [ text plainTask.name ] ]
        , td [] [ label [] [ plainTask.taskKind |> TaskKind.toString |> text ] ]
        , td [] [ label [] [ Progress.display plainTask.taskKind plainTask.progress |> text ] ]
        , td [] [ label [] [ plainTask.unit |> Maybe.withDefault "" |> text ] ]
        , td [] [ label [] [ plainTask.weight |> Positive.toString |> text ] ]
        , td [] [ button [ class "button", onClick (EnterEditPlainTaskAt plainTask.id) ] [ text language.edit ] ]
        , td [] [ button [ class "button", onClick (DeletePlainTaskAt plainTask.id) ] [ text language.remove ] ]
        ]


editPlainTaskLine : Language.TaskEditor -> TaskId -> PlainUpdateClientInput -> Html Msg
editPlainTaskLine language taskId plainUpdateClientInput =
    let
        progressClientInputByTaskKind : TaskKind -> ProgressClientInput
        progressClientInputByTaskKind taskKind =
            case taskKind of
                TaskKind.Percentual ->
                    ProgressClientInput.from FromInput.percentualProgress

                _ ->
                    ProgressClientInput.default

        taskKindRadioButton : TaskKind -> String -> ButtonGroup.RadioButtonItem Msg
        taskKindRadioButton taskKind description =
            ButtonGroup.radioButton (plainUpdateClientInput.taskKind == taskKind)
                [ Button.primary
                , Button.onClick
                    (plainUpdateClientInput
                        |> PlainUpdateClientInput.taskKind.set taskKind
                        |> PlainUpdateClientInput.progress.set (progressClientInputByTaskKind taskKind)
                        |> UpdatePlainTask taskId
                    )
                ]
                [ text description ]

        progressReachedLens : Lens PlainUpdateClientInput (FromInput Natural)
        progressReachedLens =
            PlainUpdateClientInput.progress
                |> Compose.lensWithLens ProgressClientInput.reached

        adjustPercentual : Natural -> Positive -> String
        adjustPercentual reached reachable =
            let
                magnitudeOver =
                    reachable |> Positive.toString |> String.length |> (\l -> l - 3)

                reachedString =
                    Natural.toString reached

                reachedLength =
                    String.length reachedString

                additionalZeroes =
                    max 0 (1 + magnitudeOver - reachedLength)
            in
            reachedString
                |> String.append (String.repeat additionalZeroes "0")
                |> String.toList
                |> (\l -> List.Extra.splitAt (List.length l - magnitudeOver) l)
                |> (\( n, mantissa ) ->
                        if List.isEmpty mantissa then
                            n

                        else
                            List.concat [ n, [ ',' ], mantissa ]
                   )
                |> String.fromList

        viewProgress : TaskKind -> List (Html Msg)
        viewProgress taskKind =
            case taskKind of
                -- todo: Change behaviour so switching task kinds performs some kind of translation correctly.
                TaskKind.Discrete ->
                    let
                        reachableNatural =
                            Positive.toNatural plainUpdateClientInput.progress.reachable.value

                        completed =
                            reachableNatural == plainUpdateClientInput.progress.reached.value

                        complement =
                            if completed then
                                Natural.zero

                            else
                                reachableNatural

                        complementInput =
                            FromInput.value.set complement FromInput.natural
                    in
                    [ input
                        [ type_ "checkbox"
                        , checked completed
                        , onClick
                            (plainUpdateClientInput
                                |> progressReachedLens.set complementInput
                                |> UpdatePlainTask taskId
                            )
                        ]
                        []
                    ]

                -- todo: There is a bug here, since entering a decimal point generates an internal
                --       zero, which leads to the input "1", "1.", "1.02".
                --       Either the correct behaviour should be reached via an additional parameter,
                --       or the entire input of decimal numbers should be refactored.
                --       It is tempting to use two fields (whole part, and mantissa),
                --       but there need to be interactions between the two fields, which make
                --       such a solution less straightforward as one might think.
                TaskKind.Percentual ->
                    [ input
                        [ onInput
                            (flip (FromInput.lift (PlainUpdateClientInput.progress |> Compose.lensWithIso percentualIso)).set plainUpdateClientInput
                                >> UpdatePlainTask taskId
                            )
                        , adjustPercentual plainUpdateClientInput.progress.reached.value plainUpdateClientInput.progress.reachable.value
                            |> value
                        ]
                        []
                    , label [] [ text "%" ]
                    ]

                TaskKind.Fractional ->
                    [ input
                        [ onInput
                            (flip (FromInput.lift progressReachedLens).set plainUpdateClientInput
                                >> UpdatePlainTask taskId
                            )
                        , plainUpdateClientInput.progress.reached.value
                            |> Natural.toString
                            |> value
                        ]
                        []
                    , label [] [ text "/" ]
                    , input
                        [ onInput
                            (\n ->
                                (FromInput.lift
                                    (PlainUpdateClientInput.progress
                                        |> Compose.lensWithLens ProgressClientInput.reachable
                                    )
                                ).set
                                    n
                                    plainUpdateClientInput
                                    |> (\pci ->
                                            Natural.fromString n
                                                |> Maybe.withDefault ((progressReachedLens |> Compose.lensWithLens FromInput.value).get pci)
                                                |> (\k ->
                                                        progressReachedLens.set
                                                            (FromInput.limitTo k (progressReachedLens.get pci))
                                                            pci
                                                   )
                                       )
                                    |> UpdatePlainTask taskId
                            )
                        , plainUpdateClientInput.progress.reachable.value
                            |> Positive.toString
                            |> value
                        ]
                        []
                    ]

        -- todo: Units make sense only for fractional task kind
        viewUnit : List (Html Msg)
        viewUnit =
            [ input
                [ onInput
                    (Just
                        >> Maybe.Extra.filter (String.isEmpty >> not)
                        >> flip PlainUpdateClientInput.unit.set plainUpdateClientInput
                        >> UpdatePlainTask taskId
                    )
                , plainUpdateClientInput.unit
                    |> OptionalArgumentUtil.toMaybe
                    |> Maybe.withDefault ""
                    |> value
                ]
                []
            ]

        viewWeight : Html Msg
        viewWeight =
            input
                [ value
                    (plainUpdateClientInput.weight.value
                        |> Positive.toString
                    )
                , onInput
                    (flip (FromInput.lift PlainUpdateClientInput.weight).set plainUpdateClientInput
                        >> UpdatePlainTask taskId
                    )
                ]
                []
    in
    div [ class "plainTaskLine" ]
        [ div [ class "plainName" ]
            [ label [] [ text language.plainTaskName ]
            , input
                [ value plainUpdateClientInput.name
                , onInput (flip PlainUpdateClientInput.name.set plainUpdateClientInput >> UpdatePlainTask taskId)
                ]
                []
            ]
        , div [ class "plainTaskKindArea" ]
            [ label [] [ text language.taskKind ]
            , div [ class "plainTaskKind" ]
                [ ButtonGroup.radioButtonGroup []
                    [ taskKindRadioButton TaskKind.Discrete language.discrete
                    , taskKindRadioButton TaskKind.Percentual language.percentual
                    , taskKindRadioButton TaskKind.Fractional language.fractional
                    ]
                ]
            ]
        , div [ class "plainProgressArea" ]
            [ label [] [ text language.progress ]
            , div [ class "plainProgress" ] (viewProgress plainUpdateClientInput.taskKind)
            ]
        , div [ class "plainUnit" ] viewUnit
        , div [ class "weightArea" ]
            [ label [] [ text language.weight ]
            , div [ class "weight" ] [ viewWeight ]
            ]
        , button [ class "button", onClick (SavePlainTaskEdit taskId) ]
            [ text language.save ]
        , button [ class "button", onClick (ExitEditPlainTaskAt taskId) ]
            [ text language.cancel ]
        ]


taskIdIs : TaskId -> Either PlainTask (Editing PlainTask PlainUpdateClientInput) -> Bool
taskIdIs taskId =
    Either.unpack
        (\pt -> pt.id == taskId)
        (\e -> e.original.id == taskId)


defaultPlainTaskCreation : PlainCreation
defaultPlainTaskCreation =
    { name = ""
    , taskKind = TaskKind.Percentual
    , unit = OptionalArgument.fromMaybe Nothing
    , progress = ProgressClientInput.to ProgressClientInput.default
    , weight = Positive.one
    }


addPlainTask : Model -> Cmd Msg
addPlainTask model =
    Mutation.addPlainTask
        { projectId = { uuid = model.projectInformation.id |> ProjectId.uuid }
        , plainCreation = defaultPlainTaskCreation
        }
        (LondoGQL.Object.Plain.id LondoGQL.Object.TaskId.uuid)
        |> RequestUtil.mutateWith (graphQLRequestParametersOf model GotAddPlainTaskResponse)


savePlainTask : Model -> PlainUpdate -> TaskId -> Cmd Msg
savePlainTask model plainUpdate taskId =
    Mutation.updatePlainTask
        { taskKey =
            { projectId = { uuid = model.projectInformation.id |> ProjectId.uuid }
            , taskId = { uuid = TaskId.uuid taskId }
            }
        , plainUpdate = plainUpdate
        }
        plainTaskSelection
        |> RequestUtil.mutateWith (graphQLRequestParametersOf model (GotSavePlainTaskResponse taskId))


fetchProjectData : Model -> Cmd Msg
fetchProjectData model =
    Query.fetchProject { projectId = { uuid = model.projectInformation.id |> ProjectId.uuid } }
        (SelectionSet.map2
            ProjectData
            (SelectionSet.map4
                (\n d o f ->
                    { id = model.projectInformation.id
                    , name = n
                    , description = d
                    , ownerId = o
                    , flatIfSingleTask = f
                    }
                )
                LondoGQL.Object.Project.name
                LondoGQL.Object.Project.description
                (LondoGQL.Object.Project.ownerId LondoGQL.Object.UserId.uuid |> SelectionSet.map UserId)
                LondoGQL.Object.Project.flatIfSingleTask
            )
            (LondoGQL.Object.Project.plainTasks plainTaskSelection)
        )
        |> RequestUtil.queryWith (graphQLRequestParametersOf model GotFetchProjectDataResponse)


deletePlainTask : Model -> TaskId -> Cmd Msg
deletePlainTask model taskId =
    Mutation.removePlainTask
        { taskKey =
            { projectId = { uuid = model.projectInformation.id |> ProjectId.uuid }
            , taskId = { uuid = TaskId.uuid taskId }
            }
        }
        (SelectionSet.map TaskId (LondoGQL.Object.Plain.id LondoGQL.Object.TaskId.uuid))
        |> RequestUtil.mutateWith (graphQLRequestParametersOf model GotDeletePlainTaskResponse)


plainTaskSelection : SelectionSet.SelectionSet PlainTask LondoGQL.Object.Plain
plainTaskSelection =
    SelectionSet.map6
        (\id name taskKind unit progress weight ->
            { id = id
            , name = name
            , taskKind = taskKind
            , unit = unit
            , progress = progress
            , weight = weight
            }
        )
        (SelectionSet.map TaskId (LondoGQL.Object.Plain.id LondoGQL.Object.TaskId.uuid))
        LondoGQL.Object.Plain.name
        LondoGQL.Object.Plain.taskKind
        LondoGQL.Object.Plain.unit
        (LondoGQL.Object.Plain.progress
            (SelectionSet.map2
                (\reached reachable ->
                    { reached = reached
                    , reachable = reachable
                    }
                )
                (SelectionSet.map Natural (LondoGQL.Object.Progress.reached LondoGQL.Object.Natural.nonNegative))
                (SelectionSet.map Positive (LondoGQL.Object.Progress.reachable LondoGQL.Object.Positive.positive))
            )
        )
        (SelectionSet.map Positive (LondoGQL.Object.Plain.weight LondoGQL.Object.Positive.positive))


graphQLRequestParametersOf : Model -> (RequestUtil.GraphQLDataOrError a -> Msg) -> RequestUtil.GraphQLRequestParameters a Msg
graphQLRequestParametersOf model gotResponse =
    { endpoint = model.configuration.graphQLEndpoint
    , token = model.token
    , gotResponse = gotResponse
    }
