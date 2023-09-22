module Pages.Overview.View exposing (view)

import Addresses.Frontend
import Configuration exposing (Configuration)
import Html exposing (Html, p, text)
import Pages.Overview.Page as Page
import Pages.Util.Links as Links
import Pages.Util.Style as Style
import Pages.Util.ViewUtil as ViewUtil
import Pages.View.Tristate as Tristate


view : Page.Model -> Html Page.Msg
view =
    Tristate.view
        { viewMain = viewMain
        , showLoginRedirect = True
        }


viewMain : Configuration -> Page.Main -> Html Page.LogicMsg
viewMain configuration main =
    ViewUtil.viewMainWith
        { configuration = configuration
        , currentPage = Just ViewUtil.Overview
        , showNavigation = False
        }
    <|
        p [ Style.ids.overview ]
            [ p []
                [ Links.linkButton
                    { url = Links.frontendPage configuration <| Addresses.Frontend.projects.address <| ()
                    , attributes = [ Style.classes.button.overview ]
                    , children = [ text <| main.language.projects ]
                    }
                ]
            , p []
                [ Links.linkButton
                    { url = Links.frontendPage configuration <| Addresses.Frontend.dashboards.address <| ()
                    , attributes = [ Style.classes.button.overview ]
                    , children = [ text <| main.language.dashboards ]
                    }
                ]
            , p []
                [ Links.linkButton
                    { url = Links.frontendPage configuration <| Addresses.Frontend.userSettings.address <| ()
                    , attributes = [ Style.classes.button.overview ]
                    , children = [ text <| main.language.settings ]
                    }
                ]
            ]
