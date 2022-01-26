module Pages.Project.TaskEditor exposing (..)

import Basics.Extra exposing (flip)
import Bootstrap.Button as Button
import Bootstrap.ButtonGroup as ButtonGroup
import Configuration exposing (Configuration)
import GraphQLFunctions.Lens.ProjectReferenceCreation as ProjectReferenceCreation
import GraphQLFunctions.OptionalArgumentUtil as OptionalArgumentUtil
import Html exposing (Html, button, div, input, label, text)
import Html.Attributes exposing (checked, class, for, id, type_, value)
import Html.Events exposing (onClick, onInput)
import Language.Language as Language
import List.Extra
import LondoGQL.Enum.TaskKind as TaskKind exposing (TaskKind)
import LondoGQL.Scalar exposing (Natural, Positive, Uuid(..))
import Maybe.Extra
import Monocle.Common exposing (list)
import Monocle.Compose as Compose
import Monocle.Iso exposing (Iso)
import Monocle.Lens exposing (Lens)
import Pages.Project.PlainCreationClientInput as PlainCreationClientInput
import Pages.Util.FromInput as FromInput exposing (FromInput)
import Pages.Util.ScalarUtil as ScalarUtil
import Types.PlainTask exposing (PlainTask)
import Types.Project exposing (Project)
import Types.ProjectReferenceTask exposing (ProjectReferenceTask)


type alias Model =
    { token : String
    , configuration : Configuration
    , language : Language.TaskEditor
    , project : Project
    , plainTasks : List PlainTask
    , projectReferenceTasks : List ProjectReferenceTask
    }


type Msg
    = AddPlainTask
    --| SetPlainTaskAt Int PlainCreationClientInput
    | DeletePlainTaskAt Int
    | AddProjectReferenceTask
    --| SetProjectReferenceTaskAt Int ProjectReferenceCreation
    | DeleteProjectReferenceTaskAt Int


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        AddPlainTask ->
            ( model |> plainTasksLens.set (PlainCreationClientInput.default :: model.plainTasks), Cmd.none )

        SetPlainTaskAt pos plainCreationClientInput ->
            ( model
                |> (plainTasksLens |> Compose.lensWithOptional (list pos)).set plainCreationClientInput
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
    div [ id "creatingProjectView" ]
        [ div [ id "creatingProject" ]
            [ label [ for "projectName" ] [ text model.language.projectName ]
            , label
                [ value model.project.name ]
                []
            ]
        , div [ id "creatingPlainTasks" ]
            (button [ class "button", onClick AddPlainTask ] [ text model.language.newPlainTask ]
                :: List.indexedMap (editPlainTaskLine model.language) model.plainTasks
            )
        , div [ id "creatingProjectReferenceTasks" ]
            (button [ class "button", onClick AddProjectReferenceTask ] [ text model.language.newProjectReferenceTask ]
                :: List.indexedMap (editProjectReferenceTaskLine model.language) model.projectReferenceTasks
            )
        ]


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


plainTasksLens : Lens Model (List PlainCreationClientInput)
plainTasksLens =
    Lens .plainTasks (\b a -> { a | plainTasks = b })


projectReferenceTasksLens : Lens Model (List ProjectReferenceCreation)
projectReferenceTasksLens =
    Lens .projectReferenceTasks (\b a -> { a | projectReferenceTasks = b })



-- todo: Add control for adding a reference via UUID while displaying the reference name.
-- The same control can be used for user selection elsewhere.


editPlainTaskLine : Language.TaskEditor -> Int -> PlainCreationClientInput -> Html Msg
editPlainTaskLine language pos plainCreationClientInput =
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
            ButtonGroup.radioButton (plainCreationClientInput.taskKind == taskKind)
                [ Button.primary
                , Button.onClick
                    (plainCreationClientInput
                        |> PlainCreationClientInput.taskKind.set taskKind
                        |> PlainCreationClientInput.progress.set (progressClientInputByTaskKind taskKind)
                        |> SetPlainTaskAt pos
                    )
                ]
                [ text description ]

        progressReachedLens : Lens PlainCreationClientInput (FromInput Natural)
        progressReachedLens =
            PlainCreationClientInput.progress
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
                            ScalarUtil.positiveToNatural plainCreationClientInput.progress.reachable.value

                        completed =
                            reachableNatural == plainCreationClientInput.progress.reached.value

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
                            (plainCreationClientInput
                                |> progressReachedLens.set complementInput
                                |> SetPlainTaskAt pos
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
                            (flip (FromInput.lift (PlainCreationClientInput.progress |> Compose.lensWithIso percentualIso)).set plainCreationClientInput
                                >> SetPlainTaskAt pos
                            )
                        , adjustPercentual plainCreationClientInput.progress.reached.value plainCreationClientInput.progress.reachable.value
                            |> value
                        ]
                        []
                    , label [] [ text "%" ]
                    ]

                TaskKind.Fractional ->
                    [ input
                        [ onInput
                            (flip (FromInput.lift progressReachedLens).set plainCreationClientInput
                                >> SetPlainTaskAt pos
                            )
                        , plainCreationClientInput.progress.reached.value
                            |> ScalarUtil.naturalToString
                            |> value
                        ]
                        []
                    , label [] [ text "/" ]
                    , input
                        [ onInput
                            (\n ->
                                (FromInput.lift
                                    (PlainCreationClientInput.progress
                                        |> Compose.lensWithLens ProgressClientInput.reachable
                                    )
                                ).set
                                    n
                                    plainCreationClientInput
                                    |> (\pci ->
                                            ScalarUtil.stringToNatural n
                                                |> Maybe.withDefault ((progressReachedLens |> Compose.lensWithLens FromInput.value).get pci)
                                                |> (\k ->
                                                        progressReachedLens.set
                                                            (FromInput.limitTo k (progressReachedLens.get pci))
                                                            pci
                                                   )
                                       )
                                    |> SetPlainTaskAt pos
                            )
                        , plainCreationClientInput.progress.reachable.value
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
                        >> flip PlainCreationClientInput.unit.set plainCreationClientInput
                        >> SetPlainTaskAt pos
                    )
                , plainCreationClientInput.unit
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
                    (plainCreationClientInput.weight.value
                        |> ScalarUtil.positiveToString
                    )
                , onInput
                    (flip (FromInput.lift PlainCreationClientInput.weight).set plainCreationClientInput
                        >> SetPlainTaskAt pos
                    )
                ]
                []
    in
    div [ class "plainTaskLine" ]
        [ div [ class "plainName" ]
            [ label [] [ text language.plainTaskName ]
            , input
                [ value plainCreationClientInput.name
                , onInput (flip PlainCreationClientInput.name.set plainCreationClientInput >> SetPlainTaskAt pos)
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
            , div [ class "plainProgress" ] (viewProgress plainCreationClientInput.taskKind)
            ]
        , div [ class "plainUnit" ] viewUnit
        , div [ class "weightArea" ]
            [ label [] [ text language.weight ]
            , div [ class "weight" ] [ viewWeight ]
            ]
        , button [ class "button", onClick (DeletePlainTaskAt pos) ]
            [ text language.remove ]
        ]


editProjectReferenceTaskLine : Language.TaskEditor -> Int -> ProjectReferenceCreation -> Html Msg
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
                , projectReferenceCreation.projectReferenceId.uuid
                    |> ScalarUtil.uuidToString
                    |> value
                ]
                []
            ]
        , div [ class "weightArea" ]
            [ label [] [ text language.weight ]
            , input
                [ projectReferenceCreation.weight |> ScalarUtil.positiveToString |> value
                , type_ "number"
                , Html.Attributes.min "1"
                , onInput
                    (Positive
                        >> flip ProjectReferenceCreation.weight.set projectReferenceCreation
                        >> SetProjectReferenceTaskAt pos
                    )
                , projectReferenceCreation.weight
                    |> ScalarUtil.positiveToString
                    |> value
                ]
                []
            ]
        , button [ class "button", onClick (DeleteProjectReferenceTaskAt pos) ]
            [ text language.remove ]
        ]
