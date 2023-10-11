module Pages.Util.NavigationUtil exposing (..)

import Addresses.Frontend
import Configuration exposing (Configuration)
import Html
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Types.Project.Id


projectEditorLinkButton : Configuration -> Types.Project.Id.Id -> String -> Html.Html msg
projectEditorLinkButton configuration projectId editorLabel =
    Links.linkButton
        { url = Links.frontendPage configuration <| Addresses.Frontend.tasks.address <| projectId
        , attributes = [ Style.classes.button.editor ]
        , linkText = editorLabel
        }
