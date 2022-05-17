module Pages.Project.TaskEditor exposing (..)

import Basics.Extra exposing (flip)
import Bootstrap.Button as Button
import Bootstrap.ButtonGroup as ButtonGroup
import Configuration exposing (Configuration)
import Either exposing (Either(..))
import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import Graphql.OptionalArgument as OptionalArgument
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import Html exposing (Html, button, div, input, label, text)
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
import Types.Project exposing (Project)
import Types.ProjectId as ProjectId exposing (ProjectId(..))
import Types.TaskId as TaskId exposing (TaskId(..))
import Types.UserId exposing (UserId(..))
import Util.Editing as Editing exposing (Editing)


type alias Model =
    { token : String
    , configuration : Configuration
    , language : Language.TaskEditor
    , project : Project
    , plainTasks : List (Either PlainTask (Editing PlainTask PlainUpdateClientInput))
    }



-- todo: Consider using taskIds in all message types


type Msg
    = AddPlainTask
    | GotAddPlainTaskResponse (RequestUtil.GraphQLDataOrError Uuid)
    | UpdatePlainTask Int PlainUpdateClientInput
    | SavePlainTaskEdit Int
    | GotSavePlainTaskResponse Int (RequestUtil.GraphQLDataOrError PlainTask)
    | EnterEditPlainTaskAt Int
    | ExitEditPlainTaskAt Int
    | DeletePlainTaskAt Int
    | GotDeletePlainTaskResponse (RequestUtil.GraphQLDataOrError TaskId)
    | GotFetchPlainTasksResponse (RequestUtil.GraphQLDataOrError (List PlainTask))
    | GotProjectInformationResponse (RequestUtil.GraphQLDataOrError Project)


type alias Flags =
    { projectId : ProjectId
    , token : String
    , configuration : Configuration
    , language : Language
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
            , project =
                { id = flags.projectId
                , name = ""
                , description = Nothing
                , ownerId = UserId defaultUuid
                , flatIfSingleTask = False
                }
            , plainTasks = []
            }
    in
    ( model, Cmd.batch [ fetchProjectInformation model, fetchPlainTasks model ] )


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

        UpdatePlainTask pos plainUpdateClientInput ->
            ( model
                |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (list pos)) (Either.mapRight (Editing.updateLens.set plainUpdateClientInput))
            , Cmd.none
            )

        SavePlainTaskEdit pos ->
            let
                cmd =
                    Maybe.Extra.unwrap
                        Cmd.none
                        (Either.unwrap Cmd.none (\editing -> savePlainTask model (PlainUpdateClientInput.to editing.update) editing.original.id pos))
                        (List.Extra.getAt pos model.plainTasks)
            in
            ( model, cmd )

        GotSavePlainTaskResponse pos remoteData ->
            case remoteData of
                Success plainTask ->
                    ( model
                        |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (list pos)) (Either.andThenRight (always (Left plainTask)))
                    , Cmd.none
                    )

                -- todo: Handle error case
                _ ->
                    ( model, Cmd.none )

        EnterEditPlainTaskAt pos ->
            ( model
                |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (list pos)) (Either.unpack (\pt -> { original = pt, update = PlainUpdateClientInput.from pt }) identity >> Right)
            , Cmd.none
            )

        ExitEditPlainTaskAt pos ->
            ( model |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (list pos)) (Either.unpack identity .original >> Left), Cmd.none )

        -- todo: The actual deletion in the backend is missing
        DeletePlainTaskAt pos ->
            ( model
            , deletePlainTask model pos
            )

        GotFetchPlainTasksResponse remoteData ->
            case remoteData of
                Success plainTasks ->
                    ( model |> plainTasksLens.set (plainTasks |> List.map Left), Cmd.none )

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

        GotProjectInformationResponse remoteData ->
            case remoteData of
                Success project ->
                    ( model |> projectLens.set project, Cmd.none )

                -- todo: Handle error case
                _ ->
                    ( model, Cmd.none )


view : Model -> Html Msg
view model =
    let
        viewEditPlainTasks =
            List.indexedMap
                (\i ->
                    Either.unpack (editOrDeletePlainTaskLine model.language i) (.update >> editPlainTaskLine model.language i)
                )
    in
    div [ id "creatingProjectView" ]
        (div [ id "creatingProject" ]
            [ label [ for "projectName" ] [ text model.language.projectName ]
            , label
                []
                [ text model.project.name ]
            ]
            :: div [ id "addPlainTask" ] [ button [ class "button", onClick AddPlainTask ] [ text model.language.newPlainTask ] ]
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


projectLens : Lens Model Project
projectLens =
    Lens .project (\b a -> { a | project = b })


plainTasksLens : Lens Model (List (Either PlainTask (Editing PlainTask PlainUpdateClientInput)))
plainTasksLens =
    Lens .plainTasks (\b a -> { a | plainTasks = b })



--todo: Add tabular display to editor lines; possibly reuse implementation on project page


editOrDeletePlainTaskLine : Language.TaskEditor -> Int -> PlainTask -> Html Msg
editOrDeletePlainTaskLine language pos plainTask =
    div [ id "editingPlainTask" ]
        [ label [] [ text plainTask.name ]
        , button [ class "button", onClick (EnterEditPlainTaskAt pos) ] [ text language.edit ]
        , button [ class "button", onClick (DeletePlainTaskAt pos) ] [ text language.remove ]
        ]


editPlainTaskLine : Language.TaskEditor -> Int -> PlainUpdateClientInput -> Html Msg
editPlainTaskLine language pos plainUpdateClientInput =
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
                        |> UpdatePlainTask pos
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
                                |> UpdatePlainTask pos
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
                                >> UpdatePlainTask pos
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
                                >> UpdatePlainTask pos
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
                                    |> UpdatePlainTask pos
                            )
                        , plainUpdateClientInput.progress.reachable.value
                            |> Positive.toString
                            |> value
                        ]
                        []
                    ]

        viewUnit : List (Html Msg)
        viewUnit =
            [ input
                [ onInput
                    (Just
                        >> Maybe.Extra.filter (String.isEmpty >> not)
                        >> flip PlainUpdateClientInput.unit.set plainUpdateClientInput
                        >> UpdatePlainTask pos
                    )
                , plainUpdateClientInput.unit
                    |> OptionalArgumentUtil.toMaybe
                    |> Maybe.withDefault ""
                    |> value
                ]
                []
            ]

        -- todo: Units make sense only for fractional task kind
        viewWeight : Html Msg
        viewWeight =
            input
                [ value
                    (plainUpdateClientInput.weight.value
                        |> Positive.toString
                    )
                , onInput
                    (flip (FromInput.lift PlainUpdateClientInput.weight).set plainUpdateClientInput
                        >> UpdatePlainTask pos
                    )
                ]
                []
    in
    div [ class "plainTaskLine" ]
        [ div [ class "plainName" ]
            [ label [] [ text language.plainTaskName ]
            , input
                [ value plainUpdateClientInput.name
                , onInput (flip PlainUpdateClientInput.name.set plainUpdateClientInput >> UpdatePlainTask pos)
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
        , button [ class "button", onClick (SavePlainTaskEdit pos) ]
            [ text language.save ]
        , button [ class "button", onClick (ExitEditPlainTaskAt pos) ]
            [ text language.cancel ]
        ]


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
        { projectId = { uuid = model.project.id |> ProjectId.uuid }
        , plainCreation = defaultPlainTaskCreation
        }
        (LondoGQL.Object.Plain.id LondoGQL.Object.TaskId.uuid)
        |> RequestUtil.mutateWith (graphQLRequestParametersOf model GotAddPlainTaskResponse)


savePlainTask : Model -> PlainUpdate -> TaskId -> Int -> Cmd Msg
savePlainTask model plainUpdate taskId pos =
    Mutation.updatePlainTask
        { taskKey =
            { projectId = { uuid = model.project.id |> ProjectId.uuid }
            , taskId = { uuid = TaskId.uuid taskId }
            }
        , plainUpdate = plainUpdate
        }
        plainTaskSelection
        |> RequestUtil.mutateWith (graphQLRequestParametersOf model (GotSavePlainTaskResponse pos))


fetchPlainTasks : Model -> Cmd Msg
fetchPlainTasks model =
    Query.fetchProject { projectId = { uuid = model.project.id |> ProjectId.uuid } }
        (LondoGQL.Object.Project.plainTasks plainTaskSelection)
        |> RequestUtil.queryWith (graphQLRequestParametersOf model GotFetchPlainTasksResponse)


fetchProjectInformation : Model -> Cmd Msg
fetchProjectInformation model =
    Query.fetchProject { projectId = { uuid = model.project.id |> ProjectId.uuid } }
        (SelectionSet.map4
            (\n d o f ->
                { id = model.project.id
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
        |> RequestUtil.queryWith (graphQLRequestParametersOf model GotProjectInformationResponse)


deletePlainTask : Model -> Int -> Cmd Msg
deletePlainTask model pos =
    List.Extra.getAt pos model.plainTasks
        |> Maybe.andThen Either.leftToMaybe
        |> Maybe.Extra.unwrap Cmd.none
            (\task ->
                Mutation.removePlainTask
                    { taskKey =
                        { projectId = { uuid = model.project.id |> ProjectId.uuid }
                        , taskId = { uuid = TaskId.uuid task.id }
                        }
                    }
                    (SelectionSet.map TaskId (LondoGQL.Object.Plain.id LondoGQL.Object.TaskId.uuid))
                    |> RequestUtil.mutateWith (graphQLRequestParametersOf model GotDeletePlainTaskResponse)
            )


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
