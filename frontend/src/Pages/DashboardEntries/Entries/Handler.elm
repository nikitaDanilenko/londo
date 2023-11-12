module Pages.DashboardEntries.Entries.Handler exposing (..)

import Pages.DashboardEntries.Entries.Page as Page
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.Choice.Handler
import Pages.Util.Choice.Page
import Types.DashboardEntry.Creation
import Types.DashboardEntry.Entry
import Types.DashboardEntry.Id exposing (Id(..))
import Types.Project.Id
import Types.Project.Project


initialFetch : AuthorizedAccess -> Cmd Page.LogicMsg
initialFetch =
    Types.Project.Project.fetchAllWith Pages.Util.Choice.Page.GotFetchChoicesResponse


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic =
    Pages.Util.Choice.Handler.updateLogic
        { idOfElement = .projectId
        , elementIdOrdering = Types.Project.Id.ordering
        , idOfUpdate = .projectId
        , idOfChoice = .id
        , choiceIdOrdering = Types.Project.Id.ordering
        , choiceIdOfElement = .projectId
        , choiceIdOfCreation = .projectId
        , toUpdate = identity
        , toCreation = \project dashBoardId -> Types.DashboardEntry.Creation.default dashBoardId project.id
        , createElement = \authorizedAccess _ input -> Types.DashboardEntry.Creation.createWith Pages.Util.Choice.Page.GotCreateResponse authorizedAccess input
        , saveElement = \_ _ _ -> Cmd.none
        , deleteElement =
            \authorizedAccess parentId elementId ->
                Types.DashboardEntry.Entry.deleteWith (\_ -> Pages.Util.Choice.Page.GotDeleteResponse elementId)
                    authorizedAccess
                    (Types.DashboardEntry.Id.Id
                        { dashboardId = parentId
                        , projectId = elementId
                        }
                    )
        , storeChoices = \_ -> Cmd.none
        }
