module Pages.DashboardEntries.Dashboard.Handler exposing (..)

import Addresses.Frontend
import Pages.DashboardEntries.Dashboard.Page as Page
import Pages.Util.Parent.Handler
import Pages.Util.Parent.Page
import Types.Dashboard.Dashboard
import Types.Dashboard.Update


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic =
    Pages.Util.Parent.Handler.updateLogic
        { toUpdate = Types.Dashboard.Update.from
        , idOf = .id
        , save =
            \authorizedAccess projectId ->
                Types.Dashboard.Update.updateWith
                    Pages.Util.Parent.Page.GotSaveEditResponse
                    authorizedAccess
                    projectId
                    >> Just
        , delete = Types.Dashboard.Dashboard.deleteWith (\_ -> Pages.Util.Parent.Page.GotDeleteResponse)
        , navigateAfterDeletionAddress = Addresses.Frontend.dashboards.address
        }
