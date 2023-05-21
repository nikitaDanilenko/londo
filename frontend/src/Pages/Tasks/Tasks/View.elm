module Pages.Tasks.Tasks.View exposing (..)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Dropdown exposing (dropdown)
import Html exposing (Attribute, Html, button, input, label, td, text, th, tr)
import Html.Attributes exposing (checked, disabled, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import LondoGQL.Enum.TaskKind as TaskKind exposing (TaskKind)
import Math.Natural as Natural
import Math.Positive as Positive
import Maybe.Extra
import Monocle.Compose as Compose
import Monocle.Lens as Lens exposing (Lens)
import Pages.Tasks.Tasks.Page as Page
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.ParentEditor.Page
import Pages.Util.ParentEditor.View
import Pages.Util.Style as Style
import Pages.View.Tristate as Tristate
import Types.Progress.Input
import Types.Progress.Progress as Progress exposing (Progress)
import Types.Project.ProjectId exposing (ProjectId)
import Types.Task.Creation
import Types.Task.TaskId exposing (TaskId)
import Types.Task.Update
import Util.MaybeUtil as MaybeUtil
import Util.SearchUtil as SearchUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


view : Page.Model -> Html Page.Msg
view main =
    Tristate.view
        { viewMain = viewSubMain main.projectId
        , showLoginRedirect = True
        }
        main.subModel


viewSubMain : ProjectId -> Configuration -> Page.SubMain -> Html Page.LogicMsg
viewSubMain projectId configuration subMain =
    Pages.Util.ParentEditor.View.viewParentsWith
        { currentPage = Nothing
        , matchesSearchText =
            \string task ->
                SearchUtil.search string task.name
        , sort = List.sortBy (.original >> .name) -- todo: use progress sorting
        , tableHeader = tableHeader subMain.language
        , viewLine = \language _ -> viewTaskLine language
        , updateLine = \language task -> updateTaskLine language task.id
        , deleteLine = deleteTaskLine
        , create =
            { ifCreating = createTaskLine
            , default = Types.Task.Creation.default projectId
            , label = .newTask
            , update = Pages.Util.ParentEditor.Page.UpdateCreation
            }
        , setSearchString = Pages.Util.ParentEditor.Page.SetSearchString
        , setPagination = Pages.Util.ParentEditor.Page.SetPagination
        , styling = Style.ids.addTaskView
        }
        subMain.language
        configuration
        subMain


tableHeader : Page.Language -> Html msg
tableHeader language =
    Pages.Util.ParentEditor.View.tableHeaderWith
        { columns =
            [ th [] [ label [] [ text <| language.taskName ] ]
            , th [] [ label [] [ text <| language.taskKind ] ]
            , th [] [ label [] [ text <| language.progress ] ]
            , th [] [ label [] [ text <| language.unit ] ]
            , th [] [ label [] [ text <| language.counting ] ]
            ]
        , style = Style.classes.taskEditTable
        }


viewTaskLine : Page.Language -> Page.Task -> Bool -> List (Html Page.LogicMsg)
viewTaskLine language task showControls =
    taskLineWith
        { controls =
            [ td [ Style.classes.controls ]
                [ button
                    [ Style.classes.button.edit, onClick <| Pages.Util.ParentEditor.Page.EnterEdit <| task.id ]
                    [ text <| language.edit ]
                ]
            , td [ Style.classes.controls ]
                [ button
                    [ Style.classes.button.delete, onClick <| Pages.Util.ParentEditor.Page.RequestDelete <| task.id ]
                    [ text <| language.delete ]
                ]
            ]
        , toggleMsg = Pages.Util.ParentEditor.Page.ToggleControls task.id
        , showControls = showControls
        }
        task


deleteTaskLine : Page.Language -> Page.Task -> List (Html Page.LogicMsg)
deleteTaskLine language task =
    taskLineWith
        { controls =
            [ td [ Style.classes.controls ]
                [ button [ Style.classes.button.delete, onClick <| Pages.Util.ParentEditor.Page.ConfirmDelete <| task.id ] [ text <| language.confirmDelete ] ]
            , td [ Style.classes.controls ]
                [ button
                    [ Style.classes.button.confirm, onClick <| Pages.Util.ParentEditor.Page.CancelDelete <| task.id ]
                    [ text <| language.cancel ]
                ]
            ]
        , toggleMsg = Pages.Util.ParentEditor.Page.ToggleControls <| task.id
        , showControls = True
        }
        task


taskInfoColumns : Page.Task -> List (HtmlUtil.Column msg)
taskInfoColumns task =
    [ { attributes = [ Style.classes.editable ]
      , children = [ label [] [ text task.name ] ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ label [] [ text <| TaskKind.toString <| task.taskKind ] ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ displayProgress task.progress task.taskKind ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ label [] [ text <| Maybe.withDefault "" <| task.unit ] ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ input [ type_ "checkbox", checked <| task.counting, disabled True ] [] ]
      }
    ]


taskLineWith :
    { controls : List (Html msg)
    , toggleMsg : msg
    , showControls : Bool
    }
    -> Page.Task
    -> List (Html msg)
taskLineWith ps =
    Pages.Util.ParentEditor.View.lineWith
        { rowWithControls =
            \task ->
                { display = taskInfoColumns task
                , controls = ps.controls
                }
        , toggleMsg = ps.toggleMsg
        , showControls = ps.showControls
        }


updateTaskLine : Page.Language -> TaskId -> Page.Update -> List (Html Page.LogicMsg)
updateTaskLine language taskId update =
    editProjectLineWith
        { saveMsg = Pages.Util.ParentEditor.Page.SaveEdit taskId
        , nameLens = Types.Task.Update.lenses.name
        , taskKindLens = Types.Task.Update.lenses.taskKind
        , progressLens = Types.Task.Update.lenses.progressUpdate
        , unitLens = Types.Task.Update.lenses.unit
        , countingLens = Types.Task.Update.lenses.counting
        , updateMsg = Pages.Util.ParentEditor.Page.Edit taskId
        , confirmName = language.save
        , cancelMsg = Pages.Util.ParentEditor.Page.ExitEdit <| taskId
        , cancelName = language.cancel
        , rowStyles = [ Style.classes.editLine ]
        , toggleCommand = Pages.Util.ParentEditor.Page.ToggleControls taskId |> Just
        }
        update


createTaskLine : Page.Language -> Page.Creation -> List (Html Page.LogicMsg)
createTaskLine language =
    editProjectLineWith
        { saveMsg = Pages.Util.ParentEditor.Page.Create
        , nameLens = Types.Task.Creation.lenses.name
        , taskKindLens = Types.Task.Creation.lenses.taskKind
        , progressLens = Types.Task.Creation.lenses.progress
        , unitLens = Types.Task.Creation.lenses.unit
        , countingLens = Types.Task.Creation.lenses.counting
        , updateMsg = Just >> Pages.Util.ParentEditor.Page.UpdateCreation
        , confirmName = language.newTask
        , cancelMsg = Nothing |> Pages.Util.ParentEditor.Page.UpdateCreation
        , cancelName = language.cancel
        , rowStyles = [ Style.classes.editLine ]
        , toggleCommand = Nothing
        }


editProjectLineWith :
    { saveMsg : msg
    , nameLens : Lens editedValue (ValidatedInput String)
    , taskKindLens : Lens editedValue TaskKind
    , progressLens : Lens editedValue Types.Progress.Input.ClientInput
    , unitLens : Lens editedValue (Maybe String)
    , countingLens : Lens editedValue Bool
    , updateMsg : editedValue -> msg
    , confirmName : String
    , cancelMsg : msg
    , cancelName : String
    , rowStyles : List (Attribute msg)
    , toggleCommand : Maybe msg
    }
    -> editedValue
    -> List (Html msg)
editProjectLineWith handling editedValue =
    let
        validInput =
            List.all identity
                [ handling.nameLens.get editedValue |> ValidatedInput.isValid
                , handling.progressLens.get editedValue |> .reachable |> ValidatedInput.isValid
                , handling.progressLens.get editedValue |> .reached |> ValidatedInput.isValid
                ]

        validatedSaveAction =
            MaybeUtil.optional validInput <| onEnter handling.saveMsg

        taskKindToItem taskKind =
            let
                stringValue =
                    taskKind |> TaskKind.toString
            in
            { value = stringValue
            , text = stringValue
            , enabled = True
            }

        infoColumns =
            [ td [ Style.classes.editable ]
                [ input
                    ([ MaybeUtil.defined <| value <| .text <| handling.nameLens.get <| editedValue
                     , MaybeUtil.defined <|
                        onInput <|
                            handling.updateMsg
                                << flip (ValidatedInput.lift handling.nameLens).set editedValue
                     , MaybeUtil.defined <| HtmlUtil.onEscape handling.cancelMsg
                     , validatedSaveAction
                     ]
                        |> Maybe.Extra.values
                    )
                    []
                ]
            , td []
                [ dropdown
                    { items =
                        TaskKind.list
                            |> List.map
                                taskKindToItem
                    , emptyItem = Nothing
                    , onChange =
                        Maybe.andThen TaskKind.fromString
                            >> Maybe.Extra.unwrap editedValue (flip handling.taskKindLens.set editedValue)
                            >> (\ev ->
                                    let
                                        progressModifier =
                                            case handling.taskKindLens.get ev of
                                                TaskKind.Discrete ->
                                                    Progress.toDiscrete

                                                TaskKind.Percent ->
                                                    Progress.toPercent

                                                TaskKind.Fraction ->
                                                    identity
                                    in
                                    Lens.modify (handling.progressLens |> Compose.lensWithLens Types.Progress.Input.lenses.progress)
                                        progressModifier
                                        ev
                               )
                            >> handling.updateMsg
                    }
                    []
                    (editedValue |> handling.taskKindLens.get |> TaskKind.toString |> Just)
                ]
            , td []
                (editProgress { progressLens = handling.progressLens, updateMsg = handling.updateMsg }
                    (editedValue |> handling.taskKindLens.get)
                    editedValue
                    |> List.map
                        (HtmlUtil.withAttributes
                            ([ MaybeUtil.defined <| HtmlUtil.onEscape handling.cancelMsg
                             , validatedSaveAction
                             ]
                                |> Maybe.Extra.values
                            )
                        )
                )
            , td [ Style.classes.editable ]
                [ input
                    [ value <| Maybe.withDefault "" <| handling.unitLens.get <| editedValue
                    , onInput <|
                        handling.updateMsg
                            << flip handling.unitLens.set editedValue
                            << Maybe.Extra.filter (String.isEmpty >> not)
                            << Just
                    , onEnter handling.saveMsg
                    , HtmlUtil.onEscape handling.cancelMsg
                    ]
                    []
                ]
            , td []
                [ input
                    [ type_ "checkbox"
                    , checked <| handling.countingLens.get <| editedValue
                    , onClick <|
                        handling.updateMsg <|
                            Lens.modify handling.countingLens not <|
                                editedValue
                    , onEnter handling.saveMsg
                    , HtmlUtil.onEscape handling.cancelMsg
                    ]
                    []
                ]
            ]

        controlsRow =
            Pages.Util.ParentEditor.View.controlsRowWith
                { colspan = infoColumns |> List.length
                , validInput = validInput
                , confirm =
                    { msg = handling.saveMsg
                    , name = handling.confirmName
                    }
                , cancel =
                    { msg = handling.cancelMsg
                    , name = handling.cancelName
                    }
                }

        commandToggle =
            handling.toggleCommand
                |> Maybe.Extra.toList
                |> List.map HtmlUtil.toggleControlsCell
    in
    [ tr handling.rowStyles (infoColumns ++ commandToggle)
    , controlsRow
    ]


displayProgress : Progress -> TaskKind -> Html msg
displayProgress progress taskKind =
    case taskKind of
        TaskKind.Discrete ->
            input
                [ type_ "checkbox"
                , checked <| Progress.isComplete <| progress
                , disabled True
                ]
                []

        TaskKind.Percent ->
            label []
                [ text <| flip (++) "%" <| Progress.displayPercentage <| progress
                ]

        TaskKind.Fraction ->
            label []
                [ text <| String.join "/" [ Natural.toString progress.reached, Positive.toString progress.reachable ]
                ]


editProgress :
    { progressLens : Lens editedValue Types.Progress.Input.ClientInput
    , updateMsg : editedValue -> msg
    }
    -> TaskKind
    -> editedValue
    -> List (HtmlUtil.Structure msg)
editProgress ps taskKind editedValue =
    let
        reachedLens =
            ps.progressLens |> Compose.lensWithLens Types.Progress.Input.lenses.reached

        reachableLens =
            ps.progressLens |> Compose.lensWithLens Types.Progress.Input.lenses.reachable

        progressValueLens =
            ps.progressLens
                |> Compose.lensWithLens Types.Progress.Input.lenses.progress
    in
    case taskKind of
        TaskKind.Discrete ->
            [ { constructor = input
              , attributes =
                    [ type_ "checkbox"
                    , checked <| Progress.isComplete <| progressValueLens.get <| editedValue
                    , onClick <|
                        ps.updateMsg <|
                            Lens.modify
                                progressValueLens
                                Progress.booleanToggle
                            <|
                                editedValue
                    ]
              , children = []
              }
            ]

        TaskKind.Percent ->
            let
                percentageParts =
                    editedValue
                        |> progressValueLens.get
                        |> Progress.percentParts

                whole =
                    percentageParts |> .whole

                decimal =
                    percentageParts
                        |> .decimal
                        |> Maybe.withDefault "0"
            in
            [ { constructor = input
              , attributes =
                    [ onInput <|
                        \str ->
                            let
                                fullInput =
                                    splitPercent str decimal
                            in
                            editedValue
                                |> (ValidatedInput.lift reachableLens).set fullInput.reachable
                                |> (ValidatedInput.lift reachedLens).set fullInput.reached
                                |> ps.updateMsg
                    , value <| .whole <| percentageParts
                    , Style.classes.numberCell
                    ]
              , children = []
              }
            , { constructor = label
              , attributes = []
              , children = [ text <| "." ]
              }
            , { constructor = input
              , attributes =
                    [ onInput <|
                        \str ->
                            let
                                fullInput =
                                    splitPercent whole str
                            in
                            editedValue
                                |> (ValidatedInput.lift reachableLens).set fullInput.reachable
                                |> (\ev -> Lens.modify reachedLens (ev |> reachableLens.get |> .value |> Natural.fromPositive |> ValidatedInput.updateBound) ev)
                                |> (ValidatedInput.lift reachedLens).set fullInput.reached
                                |> ps.updateMsg
                    , value <| decimal
                    , Style.classes.numberCell
                    ]
              , children = []
              }
            ]

        TaskKind.Fraction ->
            [ { constructor = input
              , attributes =
                    [ onInput <|
                        ps.updateMsg
                            << flip
                                (ValidatedInput.lift reachedLens).set
                                editedValue
                    , value <| .text <| reachedLens.get <| editedValue
                    , Style.classes.numberCell
                    ]
              , children = []
              }
            , { constructor = label
              , attributes = []
              , children = [ text <| "/" ]
              }
            , { constructor = input
              , attributes =
                    [ onInput <|
                        ps.updateMsg
                            << (\ev -> Lens.modify reachedLens (ev |> reachableLens.get |> .value |> Natural.fromPositive |> ValidatedInput.updateBound) ev)
                            << flip
                                (ValidatedInput.lift reachableLens).set
                                editedValue
                    , value <| Positive.toString <| .value <| reachableLens.get <| editedValue
                    , Style.classes.numberCell
                    ]
              , children = []
              }
            ]


splitPercent : String -> String -> { reachable : String, reached : String }
splitPercent whole decimal =
    let
        {- The behaviour for percentage is flaky when reachable = 100,
           hence we make sure that the value is always at least 1000.
        -}
        adjustedDecimal =
            if String.isEmpty decimal then
                "0"

            else
                decimal
    in
    { reachable = (Positive.oneHundred |> Positive.toString) ++ String.repeat (String.length adjustedDecimal) "0"
    , reached = whole ++ adjustedDecimal
    }
