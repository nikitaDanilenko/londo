module Pages.Overview.Handler exposing (init, update)

import Pages.Overview.Page as Page


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial flags.configuration
    , Cmd.none
    )


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update _ model =
    ( model, Cmd.none )
