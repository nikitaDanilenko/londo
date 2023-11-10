module Pages.Projects.View exposing (editProjectLineWith, projectInfoColumns, projectLineWith, tableHeader, view)

import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Attribute, Html, button, input, p, td, text, th, tr)
import Html.Attributes exposing (value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Pages.Projects.Page as Page
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.NavigationUtil as NavigationUtil
import Pages.Util.ParentEditor.Page
import Pages.Util.ParentEditor.View
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Types.Project.Creation
import Types.Project.Id exposing (Id)
import Types.Project.Update
import Util.MaybeUtil as MaybeUtil
import Util.SearchUtil as SearchUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


view : Page.Model -> List (Html Page.Msg)
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Configuration -> Page.Main -> List (Html Page.LogicMsg)
viewMain configuration main =
    Pages.Util.ParentEditor.View.viewParentsWith
        { currentPage = Just ViewUtil.Projects
        , showNavigation = True
        , matchesSearchText =
            \string project ->
                SearchUtil.search string project.name
                    || SearchUtil.search string (project.description |> Maybe.withDefault "")
        , sort = List.sortBy (.original >> .name)
        , tableHeader = tableHeader main.language
        , viewLine = viewProjectLine
        , updateLine = \language project -> updateProjectLine language project.id
        , deleteLine = deleteProjectLine
        , create =
            { ifCreating = createProjectLine
            , default = Types.Project.Creation.default
            , label = .newProject
            , update = Pages.Util.ParentEditor.Page.UpdateCreation
            }
        , setSearchString = Pages.Util.ParentEditor.Page.SetSearchString
        , setPagination = Pages.Util.ParentEditor.Page.SetPagination
        , styling = Style.ids.addProjectView
        }
        main.language
        configuration
        main


tableHeader : Page.Language -> Html msg
tableHeader language =
    Pages.Util.ParentEditor.View.tableHeaderWith
        { columns =
            [ th [] [ text <| language.name ]
            , th [] [ text <| language.description ]
            ]
        , style = Style.classes.projectEditTable
        }


viewProjectLine : Page.Language -> Configuration -> Page.Project -> Bool -> List (Html Page.LogicMsg)
viewProjectLine language configuration project showControls =
    projectLineWith
        { controls =
            [ button
                [ Style.classes.button.edit, onClick <| Pages.Util.ParentEditor.Page.EnterEdit <| project.id ]
                [ text <| language.edit ]
            , NavigationUtil.projectEditorLinkButton configuration project.id language.taskEditor
            , button
                [ Style.classes.button.delete, onClick <| Pages.Util.ParentEditor.Page.RequestDelete <| project.id ]
                [ text <| language.delete ]
            ]
        , toggleMsg = Pages.Util.ParentEditor.Page.ToggleControls project.id
        , showControls = showControls
        }
        project


deleteProjectLine : Page.Language -> Page.Project -> List (Html Page.LogicMsg)
deleteProjectLine language project =
    projectLineWith
        { controls =
            [ p []
                [ button
                    [ Style.classes.button.delete, onClick <| Pages.Util.ParentEditor.Page.ConfirmDelete <| project.id ]
                    [ text <| language.confirmDelete ]
                , button
                    [ Style.classes.button.confirm, onClick <| Pages.Util.ParentEditor.Page.CancelDelete <| project.id ]
                    [ text <| language.cancel ]
                ]
            ]
        , toggleMsg = Pages.Util.ParentEditor.Page.ToggleControls <| project.id
        , showControls = True
        }
        project


projectInfoColumns : Page.Project -> List (HtmlUtil.Column msg)
projectInfoColumns project =
    [ { attributes = [ Style.classes.editable ]
      , children = [ text project.name ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.withDefault "" <| project.description ]
      }
    ]


projectLineWith :
    { controls : List (Html msg)
    , toggleMsg : msg
    , showControls : Bool
    }
    -> Page.Project
    -> List (Html msg)
projectLineWith ps =
    Pages.Util.ParentEditor.View.lineWith
        { rowWithControls =
            \project ->
                { display = projectInfoColumns project
                , controls = ps.controls
                }
        , toggleMsg = ps.toggleMsg
        , showControls = ps.showControls
        }


updateProjectLine : Page.Language -> Id -> Page.Update -> List (Html Page.LogicMsg)
updateProjectLine language projectId projectUpdateClientInput =
    editProjectLineWith
        { saveMsg = Pages.Util.ParentEditor.Page.SaveEdit projectId
        , nameLens = Types.Project.Update.lenses.name
        , descriptionLens = Types.Project.Update.lenses.description
        , updateMsg = Pages.Util.ParentEditor.Page.Edit projectId
        , confirmName = language.save
        , cancelMsg = Pages.Util.ParentEditor.Page.ExitEdit <| projectId
        , cancelName = language.cancel
        , rowStyles = [ Style.classes.editLine ]
        , toggleCommand = Pages.Util.ParentEditor.Page.ToggleControls projectId |> Just
        }
        projectUpdateClientInput


createProjectLine : Page.Language -> Page.Creation -> List (Html Page.LogicMsg)
createProjectLine language =
    editProjectLineWith
        { saveMsg = Pages.Util.ParentEditor.Page.Create
        , nameLens = Types.Project.Creation.lenses.name
        , descriptionLens = Types.Project.Creation.lenses.description
        , updateMsg = Just >> Pages.Util.ParentEditor.Page.UpdateCreation
        , confirmName = language.add
        , cancelMsg = Nothing |> Pages.Util.ParentEditor.Page.UpdateCreation
        , cancelName = language.cancel
        , rowStyles = [ Style.classes.editLine ]
        , toggleCommand = Nothing
        }


editProjectLineWith :
    { saveMsg : msg
    , nameLens : Lens editedValue (ValidatedInput String)
    , descriptionLens : Lens editedValue (Maybe String)
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
            , td [ Style.classes.editable ]
                [ input
                    ([ MaybeUtil.defined <| value <| Maybe.withDefault "" <| handling.descriptionLens.get <| editedValue
                     , MaybeUtil.defined <|
                        onInput <|
                            flip
                                (Just
                                    >> Maybe.Extra.filter (String.isEmpty >> not)
                                    >> handling.descriptionLens.set
                                )
                                editedValue
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
