module Pages.DashboardEntries.Dashboard.View exposing (..)

import Configuration exposing (Configuration)
import Html exposing (Html, button, div, p, td, text)
import Html.Events exposing (onClick)
import Pages.DashboardEntries.Dashboard.Page as Page
import Pages.Dashboards.View
import Pages.Util.Parent.Page
import Pages.Util.Parent.View
import Pages.Util.Style as Style
import Types.Dashboard.Update


viewMain : Configuration -> Page.Main -> Html Page.LogicMsg
viewMain _ main =
    Pages.Util.Parent.View.viewMain
        { tableHeader = Pages.Dashboards.View.tableHeader
        , onView =
            \dashboard showControls ->
                Pages.Dashboards.View.dashboardLineWith
                    { controls =
                        [ div []
                            [ button
                                [ Style.classes.button.edit, Pages.Util.Parent.Page.EnterEdit |> onClick ]
                                [ text <| main.language.edit ]
                            , button
                                [ Style.classes.button.delete, Pages.Util.Parent.Page.RequestDelete |> onClick ]
                                [ text <| main.language.delete ]
                            ]
                        ]
                    , toggleMsg = Pages.Util.Parent.Page.ToggleControls
                    , showControls = showControls
                    }
                    dashboard
        , onUpdate =
            Pages.Dashboards.View.editDashboardLineWith
                { saveMsg = Pages.Util.Parent.Page.SaveEdit
                , headerLens = Types.Dashboard.Update.lenses.header
                , descriptionLens = Types.Dashboard.Update.lenses.description
                , visibilityLens = Types.Dashboard.Update.lenses.visibility
                , updateMsg = Pages.Util.Parent.Page.Edit
                , confirmName = main.language.save
                , cancelMsg = Pages.Util.Parent.Page.ExitEdit
                , cancelName = main.language.cancel
                , rowStyles = []
                , toggleCommand = Just Pages.Util.Parent.Page.ToggleControls
                }
                |> always
        , onDelete =
            Pages.Dashboards.View.dashboardLineWith
                { controls =
                    [ p []
                        [ button
                            [ Style.classes.button.delete, onClick <| Pages.Util.Parent.Page.ConfirmDelete ]
                            [ text <| main.language.confirmDelete ]
                        , button
                            [ Style.classes.button.confirm, onClick <| Pages.Util.Parent.Page.CancelDelete ]
                            [ text <| main.language.cancel ]
                        ]
                    ]
                , toggleMsg = Pages.Util.Parent.Page.ToggleControls
                , showControls = True
                }
        }
        main
