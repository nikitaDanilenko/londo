module Pages.Tasks.View exposing (..)

import Html exposing (Html, h1, text)
import Pages.Tasks.Page as Page
import Pages.Tasks.Project.View
import Pages.Tasks.Tasks.View
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate


view : Page.Model -> List (Html Page.Msg)
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Page.Main -> List (Html Page.LogicMsg)
viewMain main =
    ViewUtil.viewMainWith
        { currentPage = Nothing
        , showNavigation = True
        , id = Style.ids.taskEditor
        }
    <|
        [ Pages.Tasks.Project.View.viewMain main.project
            |> Html.map Page.ProjectMsg
        , h1 [ Style.classes.elements ] [ text <| main.tasks.language.tasks ]
        ]
            ++ (Pages.Tasks.Tasks.View.viewSubMain main.project.parent.original.id main.tasks
                    |> List.map (Html.map Page.TasksMsg)
               )
