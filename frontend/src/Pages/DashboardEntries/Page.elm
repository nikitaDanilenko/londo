module Pages.DashboardEntries.Page exposing (..)

import Language.Language
import Monocle.Lens exposing (Lens)
import Pages.DashboardEntries.Dashboard.Page
import Pages.DashboardEntries.Entries.Page
import Pages.Util.AuthorizedAccess exposing (AuthorizedAccess)
import Pages.Util.Choice.Page
import Pages.Util.Parent.Page
import Pages.View.Tristate as Tristate
import Types.Dashboard.Id
import Types.Dashboard.Resolved
import Util.HttpUtil as HttpUtil


type alias Model =
    Tristate.Model Main Initial


type alias Main =
    { dashboard : Pages.DashboardEntries.Dashboard.Page.Main
    , entries : Pages.DashboardEntries.Entries.Page.Main
    }


type alias Initial =
    { dashboard : Pages.DashboardEntries.Dashboard.Page.Initial
    , entries : Pages.DashboardEntries.Entries.Page.Initial
    }


initial : AuthorizedAccess -> Types.Dashboard.Id.Id -> Model
initial authorizedAccess dashboardId =
    { dashboard = Pages.Util.Parent.Page.initialWith authorizedAccess.jwt Language.Language.default.dashboardEditor
    , entries = Pages.Util.Choice.Page.initialWith authorizedAccess.jwt dashboardId Language.Language.default.dashboardEntryEditor
    }
        |> Tristate.createInitial authorizedAccess.configuration


initialToMain : Initial -> Maybe Main
initialToMain i =
    i.dashboard
        |> Pages.Util.Parent.Page.initialToMain
        |> Maybe.andThen
            (\dashboard ->
                i.entries
                    |> Pages.Util.Choice.Page.initialToMain
                    |> Maybe.map
                        (\entries ->
                            { dashboard = dashboard
                            , entries = entries
                            }
                        )
            )


lenses :
    { initial :
        { dashboard : Lens Initial Pages.DashboardEntries.Dashboard.Page.Initial
        , entries : Lens Initial Pages.DashboardEntries.Entries.Page.Initial
        }
    , main :
        { dashboard : Lens Main Pages.DashboardEntries.Dashboard.Page.Main
        , entries : Lens Main Pages.DashboardEntries.Entries.Page.Main
        }
    }
lenses =
    { initial =
        { dashboard = Lens .dashboard (\b a -> { a | dashboard = b })
        , entries = Lens .entries (\b a -> { a | entries = b })
        }
    , main =
        { dashboard = Lens .dashboard (\b a -> { a | dashboard = b })
        , entries = Lens .entries (\b a -> { a | entries = b })
        }
    }


type alias Flags =
    { dashboardId : Types.Dashboard.Id.Id
    , authorizedAccess : AuthorizedAccess
    }


type alias Msg =
    Tristate.Msg LogicMsg


type LogicMsg
    = GotFetchResponse (HttpUtil.GraphQLResult Types.Dashboard.Resolved.Resolved)
    | DashboardMsg Pages.DashboardEntries.Dashboard.Page.LogicMsg
    | EntriesMsg Pages.DashboardEntries.Entries.Page.LogicMsg
