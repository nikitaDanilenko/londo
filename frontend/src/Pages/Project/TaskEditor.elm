module Pages.Project.TaskEditor exposing (..)

import Basics.Extra exposing (flip)
import Bootstrap.Button as Button
import Bootstrap.ButtonGroup as ButtonGroup
import Configuration exposing (Configuration)
import Constants
import Either exposing (Either(..))
import GraphQLFunctions.Lens.ProjectReferenceCreation as ProjectReferenceCreation
import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import Graphql.Http
import Graphql.OptionalArgument as OptionalArgument
import Html exposing (Html, button, div, input, label, text)
import Html.Attributes exposing (checked, class, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Language.Language as Language
import List.Extra
import LondoGQL.Enum.TaskKind as TaskKind exposing (TaskKind)
import LondoGQL.InputObject exposing (PlainCreation, ProgressInput, ProjectReferenceCreation)
import LondoGQL.Mutation as Mutation
import LondoGQL.Object.Plain
import LondoGQL.Object.TaskId
import LondoGQL.Scalar exposing (Natural, Positive, Uuid(..))
import Maybe.Extra
import Monocle.Common exposing (list)
import Monocle.Compose as Compose
import Monocle.Iso exposing (Iso)
import Monocle.Lens as Lens exposing (Lens)
import Monocle.Optional as Optional
import Pages.Project.PlainUpdateClientInput as PlainUpdateClientInput exposing (PlainUpdateClientInput, default)
import Pages.Project.ProgressClientInput as ProgressClientInput exposing (ProgressClientInput)
import Pages.Project.ProjectReferenceUpdateClientInput as ProjectReferenceUpdateClientInput exposing (ProjectReferenceUpdateClientInput)
import Pages.Util.FromInput as FromInput exposing (FromInput)
import Pages.Util.ScalarUtil as ScalarUtil
import RemoteData exposing (RemoteData(..))
import Types.PlainTask as PlainTask exposing (PlainTask)
import Types.Project exposing (Project)
import Types.ProjectId as ProjectId exposing (ProjectId(..))
import Types.ProjectReferenceTask exposing (ProjectReferenceTask)
import Types.TaskId exposing (TaskId(..))


type alias Model =
    { token : String
    , configuration : Configuration
    , language : Language.TaskEditor
    , project : Project
    , plainTasks : List (Either PlainTask (Editing PlainTask PlainUpdateClientInput))
    , projectReferenceTasks : List (Either ProjectReferenceTask (Editing ProjectReferenceTask ProjectReferenceUpdateClientInput))
    }


type Msg
    = AddPlainTask
    | GotAddPlainTaskResponse (RemoteData (Graphql.Http.Error Uuid) Uuid)
    | UpdatePlainTask Int PlainUpdateClientInput
    | EnterEditPlainTaskAt Int PlainTask
    | ExitEditPlainTaskAt Int
    | DeletePlainTaskAt Int
    | AddProjectReferenceTask
    | UpdateProjectReferenceTask Int ProjectReferenceUpdateClientInput
    | EnterEditProjectReferenceTaskAt Int ProjectReferenceTask
    | DeleteProjectReferenceTaskAt Int



-- todo: Move to separate module


type alias Editing a b =
    { original : a
    , editing : b
    }


editingLens : Lens (Editing a b) b
editingLens =
    Lens .editing (\b a -> { a | editing = b })


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
                                                , editing = PlainUpdateClientInput.from newPlainTask
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
                |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (list pos)) (Either.mapRight (editingLens.set plainUpdateClientInput))
            , Cmd.none
            )

        EnterEditPlainTaskAt pos original ->
            ( model
                |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (list pos)) (Either.unpack (\pt -> { original = pt, editing = PlainUpdateClientInput.from pt }) identity >> Right)
            , Cmd.none
            )

        ExitEditPlainTaskAt pos ->
            ( model |> Optional.modify (plainTasksLens |> Compose.lensWithOptional (list pos)) (Either.unpack identity .original >> Left), Cmd.none )

        DeletePlainTaskAt pos ->
            ( model
                |> plainTasksLens.set
                    (model.plainTasks
                        |> List.Extra.removeAt pos
                    )
            , Cmd.none
            )

        AddProjectReferenceTask ->
            ( model |> projectReferenceTasksLens.set (Right ProjectReferenceUpdateClientInput.default :: model.projectReferenceTasks), Cmd.none )

        UpdateProjectReferenceTask pos projectReferenceUpdateClientInput ->
            ( model
                |> projectReferenceTaskCreationLens.set (Just projectReferenceUpdateClientInput)
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
        viewEditPlainTasks =
            List.indexedMap
                (\i ->
                    Either.unpack (editOrDeletePlainTaskLine model.language i) (.editing >> editPlainTaskLine model.language i)
                )

        viewEditProjectReferenceTasks =
            List.indexedMap
                (\i ->
                    Either.unpack (editOrDeleteProjectReferenceTaskLine model.language i) (.editing >> editProjectReferenceTaskLine model.language i)
                )
    in
    div [ id "creatingProjectView" ]
        (div [ id "creatingProject" ]
            [ label [ for "projectName" ] [ text model.language.projectName ]
            , label
                [ value model.project.name ]
                []
            ]
            :: (viewEditPlainTasks model.plainTasks
                    ++ viewEditProjectReferenceTasks model.projectReferenceTasks
               )
        )


defaultProjectReferenceCreation : ProjectReferenceCreation
defaultProjectReferenceCreation =
    { weight = Positive "1"
    , projectReferenceId =
        { uuid = Uuid ""
        }
    }


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


plainTasksLens : Lens Model (List (Either PlainTask (Editing PlainTask PlainUpdateClientInput)))
plainTasksLens =
    Lens .plainTasks (\b a -> { a | plainTasks = b })


projectReferenceTasksLens : Lens Model (List (Either ProjectReferenceTask (Editing ProjectReferenceTask ProjectReferenceUpdateClientInput)))
projectReferenceTasksLens =
    Lens .projectReferenceTasks (\b a -> { a | projectReferenceTasks = b })



-- todo: Add control for adding a reference via UUID while displaying the reference name.
-- The same control can be used for user selection elsewhere.


editOrDeletePlainTaskLine : Language.TaskEditor -> Int -> PlainTask -> Html Msg
editOrDeletePlainTaskLine language pos plainTask =
    div [ id "editingPlainTask" ]
        [ button [ class "button", onClick (EnterEditPlainTaskAt pos plainTask) ] [ text language.edit ]
        , button [ class "button", onClick (DeletePlainTaskAt pos) ] [ text language.cancel ]
        ]


editOrDeleteProjectReferenceTaskLine : Language.TaskEditor -> Int -> ProjectReferenceTask -> Html Msg
editOrDeleteProjectReferenceTaskLine language pos projectReferenceTask =
    div [ id "editingProjectReferenceTask" ]
        [ button [ class "button", onClick (EnterEditProjectReferenceTaskAt pos projectReferenceTask) ] [ text language.edit ]
        , button [ class "button", onClick (DeleteProjectReferenceTaskAt pos) ] [ text language.cancel ]
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
                    reachable |> ScalarUtil.positiveToString |> String.length |> (\l -> l - 3)

                reachedString =
                    ScalarUtil.naturalToString reached

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
                            ScalarUtil.positiveToNatural plainUpdateClientInput.progress.reachable.value

                        completed =
                            reachableNatural == plainUpdateClientInput.progress.reached.value

                        complement =
                            if completed then
                                ScalarUtil.zeroNatural

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
                            -- todo: Adjustment needs to take place on lift level
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
                            |> ScalarUtil.naturalToString
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
                                            ScalarUtil.stringToNatural n
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
                            |> ScalarUtil.positiveToString
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
                        |> ScalarUtil.positiveToString
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
        , button [ class "button", onClick (ExitEditPlainTaskAt pos) ]
            [ text language.remove ]
        ]


editProjectReferenceTaskLine : Language.TaskEditor -> Int -> ProjectReferenceUpdateClientInput -> Html Msg
editProjectReferenceTaskLine language pos projectReferenceUpdateClientInput =
    div [ class "projectReferenceLine" ]
        [ div [ class "projectReferenceId" ]
            [ label []
                [ text language.projectReference ]
            , input
                [ projectReferenceUpdateClientInput.projectReferenceId
                    |> ProjectId.uuid
                    |> ScalarUtil.uuidToString
                    |> value
                , onInput
                    (Uuid
                        >> ProjectId
                        >> flip ProjectReferenceUpdateClientInput.projectReferenceId.set projectReferenceUpdateClientInput
                        >> UpdateProjectReferenceTask pos
                    )
                , projectReferenceUpdateClientInput.projectReferenceId
                    |> ProjectId.uuid
                    |> ScalarUtil.uuidToString
                    |> value
                ]
                []
            ]
        , div [ class "weightArea" ]
            [ label [] [ text language.weight ]
            , input
                [ projectReferenceUpdateClientInput.weight.value |> ScalarUtil.positiveToString |> value
                , type_ "number"
                , Html.Attributes.min "1"
                , onInput
                    (flip (FromInput.lift ProjectReferenceUpdateClientInput.weight).set projectReferenceUpdateClientInput
                        >> UpdateProjectReferenceTask pos
                    )
                , projectReferenceUpdateClientInput.weight.value
                    |> ScalarUtil.positiveToString
                    |> value
                ]
                []
            ]
        , button [ class "button", onClick (DeleteProjectReferenceTaskAt pos) ]
            [ text language.remove ]
        ]


defaultPlainTaskCreation : PlainCreation
defaultPlainTaskCreation =
    { name = ""
    , taskKind = TaskKind.Percentual
    , unit = OptionalArgument.fromMaybe Nothing
    , progress = ProgressClientInput.to ProgressClientInput.default
    , weight = Positive "1"
    }


addPlainTask : Model -> Cmd Msg
addPlainTask model =
    Mutation.addPlainTask
        { projectId = { uuid = model.project.id }
        , plainCreation = defaultPlainTaskCreation
        }
        (LondoGQL.Object.Plain.id LondoGQL.Object.TaskId.uuid)
        |> Graphql.Http.mutationRequest model.configuration.graphQLEndpoint
        |> Graphql.Http.withHeader Constants.userToken model.token
        |> Graphql.Http.send (RemoteData.fromResult >> GotAddPlainTaskResponse)
