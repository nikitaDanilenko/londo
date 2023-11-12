module Pages.Tasks.View exposing (..)

import Configuration exposing (Configuration)
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


viewMain : Configuration -> Page.Main -> List (Html Page.LogicMsg)
viewMain configuration main =
    ViewUtil.viewMainWith
        { configuration = configuration
        , currentPage = Nothing
        , showNavigation = True
        , id = Style.ids.taskEditor
        }
    <|
        [ Pages.Tasks.Project.View.viewMain configuration main.project
            |> Html.map Page.ProjectMsg
        , h1 [ Style.classes.elements ] [ text <| main.tasks.language.tasks ]
        ]
            ++ (Pages.Tasks.Tasks.View.viewSubMain main.project.parent.original.id configuration main.tasks
                    |> List.map (Html.map Page.TasksMsg)
               )
