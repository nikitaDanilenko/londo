module Pages.MealEntries.Meal.Handler exposing (updateLogic)

import Addresses.Frontend
import Pages.Tasks.Project.Page as Page
import Pages.Util.Parent.Handler
import Pages.Util.Parent.Page
import Types.Project.Project
import Types.Project.Update


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic =
    Pages.Util.Parent.Handler.updateLogic
        { toUpdate = Types.Project.Update.from
        , idOf = .id
        , save =
            \authorizedAccess ->
                Types.Project.Update.updateWith
                    Pages.Util.Parent.Page.GotSaveEditResponse
                    authorizedAccess
                    >> Just
        , delete = Types.Project.Project.deleteWith (\_ -> Pages.Util.Parent.Page.GotDeleteResponse)
        , navigateAfterDeletionAddress = Addresses.Frontend.projects.address
        }
