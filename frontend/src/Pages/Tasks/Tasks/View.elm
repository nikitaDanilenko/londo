module Pages.Tasks.Tasks.View exposing (..)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Attribute, Html, button, input, label, td, text, th, tr)
import Html.Attributes exposing (checked, type_, value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import LondoGQL.Enum.TaskKind as TaskKind exposing (TaskKind)
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Pages.Tasks.Tasks.Page as Page
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.ParentEditor.Page
import Pages.Util.ParentEditor.View
import Pages.Util.Style as Style
import Pages.View.Tristate as Tristate
import Types.Progress.Input
import Types.Progress.Progress as Progress exposing (Progress)
import Types.Task.Creation
import Types.Task.TaskId exposing (TaskId)
import Types.Task.Update
import Util.MaybeUtil as MaybeUtil
import Util.SearchUtil as SearchUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


view : Page.Model -> Html Page.Msg
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Configuration -> Page.Main -> Html Page.LogicMsg
viewMain configuration main =
    Pages.Util.ParentEditor.View.viewParentsWith
        { currentPage = Nothing
        , matchesSearchText =
            \string task ->
                SearchUtil.search string task.name
        , sort = List.sortBy (.original >> .name) -- todo: use progress sorting
        , tableHeader = tableHeader main.main.language
        , viewLine = \language _ -> viewTaskLine language
        , updateLine = \language task -> updateTaskLine language task.id
        , deleteLine = deleteTaskLine
        , create =
            { ifCreating = createTaskLine
            , default = Types.Task.Creation.default main.projectId
            , label = .newTask
            , update = Pages.Util.ParentEditor.Page.UpdateCreation
            }
        , setSearchString = Pages.Util.ParentEditor.Page.SetSearchString
        , setPagination = Pages.Util.ParentEditor.Page.SetPagination
        , styling = Style.ids.addTaskView
        }
        main.main.language
        configuration
        main.main


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

      --todo: Use a prettier naming for task kinds
      , children = [ label [] [ text <| TaskKind.toString <| task.taskKind ] ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ label [] [ text <| Progress.display task.taskKind <| task.progress ] ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ label [] [ text <| Maybe.withDefault "" <| task.unit ] ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ input [ type_ "checkbox", checked <| task.counting ] [] ]
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
                ]

        validatedSaveAction =
            MaybeUtil.optional validInput <| onEnter handling.saveMsg

        infoColumns =
            [ td [ Style.classes.editable ]
                [ input
                    ([ MaybeUtil.defined <| value <| .text <| handling.nameLens.get <| editedValue
                     , MaybeUtil.defined <|
                        onInput <|
                            flip (ValidatedInput.lift handling.nameLens).set editedValue
                                >> handling.updateMsg
                     , MaybeUtil.defined <| HtmlUtil.onEscape handling.cancelMsg
                     , validatedSaveAction
                     ]
                        |> Maybe.Extra.values
                    )
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