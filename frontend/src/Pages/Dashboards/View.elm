module Pages.Dashboards.View exposing (..)

import Addresses.Frontend
import Basics.Extra exposing (flip)
import Configuration exposing (Configuration)
import Html exposing (Attribute, Html, button, input, td, text, th, tr)
import Html.Attributes exposing (value)
import Html.Events exposing (onClick, onInput)
import Html.Events.Extra exposing (onEnter)
import LondoGQL.Enum.Visibility
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Pages.Dashboards.Page as Page
import Pages.Util.HtmlUtil as HtmlUtil
import Pages.Util.Links as Links
import Pages.Util.ParentEditor.Page
import Pages.Util.ParentEditor.View
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate
import Types.Dashboard.Creation
import Types.Dashboard.Id
import Types.Dashboard.Update
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
        { currentPage = Just ViewUtil.Dashboards
        , showNavigation = True
        , matchesSearchText =
            \string dashboard ->
                SearchUtil.search string dashboard.header
                    || SearchUtil.search string (dashboard.description |> Maybe.withDefault "")
        , sort = List.sortBy (.original >> .header)
        , tableHeader = tableHeader main.language
        , viewLine = viewDashboardLine
        , updateLine = \language dashboard -> updateDashboardLine language dashboard.id
        , deleteLine = deleteDashboardLine
        , create =
            { ifCreating = createDashboardLine
            , default = Types.Dashboard.Creation.default
            , label = .newDashboard
            , update = Pages.Util.ParentEditor.Page.UpdateCreation
            }
        , setSearchString = Pages.Util.ParentEditor.Page.SetSearchString
        , setPagination = Pages.Util.ParentEditor.Page.SetPagination
        , styling = Style.ids.addDashboardView
        }
        main.language
        configuration
        main


tableHeader : Page.Language -> Html msg
tableHeader language =
    Pages.Util.ParentEditor.View.tableHeaderWith
        { columns =
            [ th [] [ text <| language.header ]
            , th [] [ text <| language.description ]
            ]
        , style = Style.classes.dashboardEditTable
        }


viewDashboardLine : Page.Language -> Configuration -> Page.Dashboard -> Bool -> List (Html Page.LogicMsg)
viewDashboardLine language configuration dashboard showControls =
    dashboardLineWith
        { controls =
            [ button
                [ Style.classes.button.edit, onClick <| Pages.Util.ParentEditor.Page.EnterEdit <| dashboard.id ]
                [ text <| language.edit ]
            , Links.linkButton
                { url = Links.frontendPage configuration <| Addresses.Frontend.dashboardEntries.address <| dashboard.id
                , attributes = [ Style.classes.button.editor ]
                , linkText = language.dashboardEntryEditor
                }
            , button
                [ Style.classes.button.delete, onClick <| Pages.Util.ParentEditor.Page.RequestDelete <| dashboard.id ]
                [ text <| language.delete ]
            ]
        , toggleMsg = Pages.Util.ParentEditor.Page.ToggleControls dashboard.id
        , showControls = showControls
        }
        dashboard


deleteDashboardLine : Page.Language -> Page.Dashboard -> List (Html Page.LogicMsg)
deleteDashboardLine language dashboard =
    dashboardLineWith
        { controls =
            [ button
                [ Style.classes.button.delete, onClick <| Pages.Util.ParentEditor.Page.ConfirmDelete <| dashboard.id ]
                [ text <| language.confirmDelete ]
            , button
                [ Style.classes.button.confirm, onClick <| Pages.Util.ParentEditor.Page.CancelDelete <| dashboard.id ]
                [ text <| language.cancel ]
            ]
        , toggleMsg = Pages.Util.ParentEditor.Page.ToggleControls <| dashboard.id
        , showControls = True
        }
        dashboard


dashboardInfoColumns : Page.Dashboard -> List (HtmlUtil.Column msg)
dashboardInfoColumns dashboard =
    [ { attributes = [ Style.classes.editable ]
      , children = [ text dashboard.header ]
      }
    , { attributes = [ Style.classes.editable ]
      , children = [ text <| Maybe.withDefault "" <| dashboard.description ]
      }
    ]


dashboardLineWith :
    { controls : List (Html msg)
    , toggleMsg : msg
    , showControls : Bool
    }
    -> Page.Dashboard
    -> List (Html msg)
dashboardLineWith ps =
    Pages.Util.ParentEditor.View.lineWith
        { rowWithControls =
            \dashboard ->
                { display = dashboardInfoColumns dashboard
                , controls = ps.controls
                }
        , toggleMsg = ps.toggleMsg
        , showControls = ps.showControls
        }


updateDashboardLine : Page.Language -> Types.Dashboard.Id.Id -> Page.Update -> List (Html Page.LogicMsg)
updateDashboardLine language dashboardId dashboardUpdateClientInput =
    editDashboardLineWith
        { saveMsg = Pages.Util.ParentEditor.Page.SaveEdit dashboardId
        , headerLens = Types.Dashboard.Update.lenses.header
        , descriptionLens = Types.Dashboard.Update.lenses.description
        , visibilityLens = Types.Dashboard.Update.lenses.visibility
        , updateMsg = Pages.Util.ParentEditor.Page.Edit dashboardId
        , confirmName = language.save
        , cancelMsg = Pages.Util.ParentEditor.Page.ExitEdit <| dashboardId
        , cancelName = language.cancel
        , rowStyles = [ Style.classes.editLine ]
        , toggleCommand = Pages.Util.ParentEditor.Page.ToggleControls dashboardId |> Just
        }
        dashboardUpdateClientInput


createDashboardLine : Page.Language -> Page.Creation -> List (Html Page.LogicMsg)
createDashboardLine language =
    editDashboardLineWith
        { saveMsg = Pages.Util.ParentEditor.Page.Create
        , headerLens = Types.Dashboard.Creation.lenses.header
        , descriptionLens = Types.Dashboard.Creation.lenses.description
        , visibilityLens = Types.Dashboard.Creation.lenses.visibility
        , updateMsg = Just >> Pages.Util.ParentEditor.Page.UpdateCreation
        , confirmName = language.add
        , cancelMsg = Nothing |> Pages.Util.ParentEditor.Page.UpdateCreation
        , cancelName = language.cancel
        , rowStyles = [ Style.classes.editLine ]
        , toggleCommand = Nothing
        }


editDashboardLineWith :
    { saveMsg : msg
    , headerLens : Lens editedValue (ValidatedInput String)
    , descriptionLens : Lens editedValue (Maybe String)
    , visibilityLens : Lens editedValue LondoGQL.Enum.Visibility.Visibility
    , updateMsg : editedValue -> msg
    , confirmName : String
    , cancelMsg : msg
    , cancelName : String
    , rowStyles : List (Attribute msg)
    , toggleCommand : Maybe msg
    }
    -> editedValue
    -> List (Html msg)
editDashboardLineWith handling editedValue =
    let
        validInput =
            List.all identity
                [ handling.headerLens.get editedValue |> ValidatedInput.isValid
                ]

        validatedSaveAction =
            MaybeUtil.optional validInput <| onEnter handling.saveMsg

        infoColumns =
            [ td [ Style.classes.editable ]
                [ input
                    ([ MaybeUtil.defined <| value <| .text <| handling.headerLens.get <| editedValue
                     , MaybeUtil.defined <|
                        onInput <|
                            flip (ValidatedInput.lift handling.headerLens).set editedValue
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
