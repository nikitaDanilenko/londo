module Pages.Tasks.Project.View exposing (viewMain)

import Configuration exposing (Configuration)
import Html exposing (Html, button, text)
import Html.Events exposing (onClick)
import Pages.Projects.View
import Pages.Tasks.Project.Page as Page
import Pages.Util.Parent.Page
import Pages.Util.Parent.View
import Pages.Util.Style as Style
import Types.Project.Update


viewMain : Configuration -> Page.Main -> Html Page.LogicMsg
viewMain _ main =
    Pages.Util.Parent.View.viewMain
        { tableHeader = Pages.Projects.View.tableHeader
        , onView =
            \project showControls ->
                Pages.Projects.View.projectLineWith
                    { controls =
                        [ button
                            [ Style.classes.button.edit, Pages.Util.Parent.Page.EnterEdit |> onClick ]
                            [ text <| main.language.edit ]
                        , button
                            [ Style.classes.button.delete, Pages.Util.Parent.Page.RequestDelete |> onClick ]
                            [ text <| main.language.delete ]
                        ]
                    , toggleMsg = Pages.Util.Parent.Page.ToggleControls
                    , showControls = showControls
                    }
                    project
        , onUpdate =
            Pages.Projects.View.editProjectLineWith
                { saveMsg = Pages.Util.Parent.Page.SaveEdit
                , nameLens = Types.Project.Update.lenses.name
                , descriptionLens = Types.Project.Update.lenses.description
                , updateMsg = Pages.Util.Parent.Page.Edit
                , confirmName = main.language.save
                , cancelMsg = Pages.Util.Parent.Page.ExitEdit
                , cancelName = main.language.cancel
                , rowStyles = []
                , toggleCommand = Just Pages.Util.Parent.Page.ToggleControls
                }
                |> always
        , onDelete =
            Pages.Projects.View.projectLineWith
                { controls =
                    [ button
                        [ Style.classes.button.delete, onClick <| Pages.Util.Parent.Page.ConfirmDelete ]
                        [ text <| main.language.confirmDelete ]
                    , button
                        [ Style.classes.button.confirm, onClick <| Pages.Util.Parent.Page.CancelDelete ]
                        [ text <| main.language.cancel ]
                    ]
                , toggleMsg = Pages.Util.Parent.Page.ToggleControls
                , showControls = True
                }
        }
        main
