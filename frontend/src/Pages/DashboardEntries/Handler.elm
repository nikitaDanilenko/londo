module Pages.DashboardEntries.Handler exposing (init, update)

import Pages.DashboardEntries.Dashboard.Handler
import Pages.DashboardEntries.Entries.Handler
import Pages.DashboardEntries.Page as Page
import Pages.Util.Choice.Page
import Pages.Util.Parent.Page
import Pages.View.Tristate as Tristate
import Pages.View.TristateUtil as TristateUtil
import Result.Extra
import Types.Dashboard.Resolved


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial flags.authorizedAccess flags.dashboardId
    , Types.Dashboard.Resolved.fetchWith Page.GotFetchResponse flags.authorizedAccess flags.dashboardId
        |> Cmd.map Tristate.Logic
    )


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        updateLogicDashboard =
            TristateUtil.updateFromSubModel
                { initialSubModelLens = Page.lenses.initial.dashboard
                , mainSubModelLens = Page.lenses.main.dashboard
                , fromInitToMain = Page.initialToMain
                , updateSubModel = Pages.DashboardEntries.Dashboard.Handler.updateLogic
                , toMsg = Page.DashboardMsg
                }

        updateLogicEntries =
            TristateUtil.updateFromSubModel
                { initialSubModelLens = Page.lenses.initial.entries
                , mainSubModelLens = Page.lenses.main.entries
                , fromInitToMain = Page.initialToMain
                , updateSubModel = Pages.DashboardEntries.Entries.Handler.updateLogic
                , toMsg = Page.EntriesMsg
                }
    in
    case msg of
        Page.EntriesMsg entriesMsg ->
            updateLogicEntries
                entriesMsg
                model

        Page.DashboardMsg mealMsg ->
            updateLogicDashboard
                mealMsg
                model

        Page.GotFetchResponse result ->
            let
                newModel =
                    result
                        |> Result.Extra.unpack (Tristate.toError model)
                            (\resolved ->
                                -- We assume that all subcommands are Cmd.none, otherwise collect the commands, and batch the result.
                                model
                                    |> updateLogicDashboard (resolved |> .dashboard |> Ok |> Pages.Util.Parent.Page.GotFetchResponse)
                                    |> Tuple.first
                                    |> updateLogicEntries (resolved |> .entries |> Ok |> Pages.Util.Choice.Page.GotFetchChoicesResponse)
                                    |> Tuple.first
                            )
            in
            ( newModel, Cmd.none )
