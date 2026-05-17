module Pages.Util.NavigationUtil exposing (..)

import Addresses.Frontend
import Html
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Types.Project.Id


projectEditorLinkButton : Types.Project.Id.Id -> String -> Html.Html msg
projectEditorLinkButton projectId editorLabel =
    Links.linkButton
        { url = Links.frontendPage <| Addresses.Frontend.tasks.address <| projectId
        , attributes = [ Style.classes.button.editor ]
        , linkText = editorLabel
        }
