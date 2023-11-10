module Pages.Overview.View exposing (view)

import Addresses.Frontend
import Configuration exposing (Configuration)
import Html exposing (Html, li, main_, menu)
import Pages.Overview.Page as Page
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Pages.View.Tristate as Tristate


view : Page.Model -> List (Html Page.Msg)
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Configuration -> Page.Main -> List (Html Page.LogicMsg)
viewMain configuration main =
    [ main_ [ Style.ids.overview ]
        [ menu []
            [ li []
                [ Links.linkButton
                    { url = Links.frontendPage configuration <| Addresses.Frontend.projects.address <| ()
                    , attributes = [ Style.classes.button.overview ]
                    , linkText = main.language.projects
                    }
                ]
            , li []
                [ Links.linkButton
                    { url = Links.frontendPage configuration <| Addresses.Frontend.dashboards.address <| ()
                    , attributes = [ Style.classes.button.overview ]
                    , linkText = main.language.dashboards
                    }
                ]
            , li []
                [ Links.linkButton
                    { url = Links.frontendPage configuration <| Addresses.Frontend.settings.address <| ()
                    , attributes = [ Style.classes.button.overview ]
                    , linkText = main.language.settings
                    }
                ]
            ]
        ]
    ]
