module Pages.Statistics.Public.Handler exposing (..)

import Language.Language
import Monocle.Compose as Compose
import Monocle.Lens exposing (Lens)
import Pages.Statistics.Page
import Pages.Statistics.Pagination as Pagination
import Pages.Statistics.Public.Page as Page
import Pages.Util.PaginationSettings as PaginationSettings
import Pages.View.Tristate as Tristate
import Result.Extra
import Types.Dashboard.Analysis


update : Page.Msg -> Page.Model -> ( Page.Model, Cmd Page.Msg )
update =
    Tristate.updateWith updateLogic


init : Page.Flags -> ( Page.Model, Cmd Page.Msg )
init flags =
    ( Page.initial
        { taskEditor = Language.Language.default.taskEditor
        , project = Language.Language.default.projectEditor
        , dashboard = Language.Language.default.dashboardEditor
        , statistics = Language.Language.default.statistics
        }
        Language.Language.default.errorHandling
        flags.configuration
    , Types.Dashboard.Analysis.fetchPublicWith
        Page.GotFetchDashboardAnalysisResponse
        flags.configuration
        flags.dashboardId
        Pages.Statistics.Page.numberOfDecimalPlaces
        |> Cmd.map Tristate.Logic
    )


updateLogic : Page.LogicMsg -> Page.Model -> ( Page.Model, Cmd Page.LogicMsg )
updateLogic msg model =
    let
        gotFetchDashboardAnalysisResponse result =
            ( result
                |> Result.Extra.unpack (Tristate.toError model)
                    (\dashboardAnalysis ->
                        model
                            |> Tristate.mapInitial
                                (Page.lenses.initial.dashboardAnalysis.set
                                    (dashboardAnalysis |> Just)
                                )
                            |> Tristate.fromInitToMain Page.initialToMain
                    )
            , Cmd.none
            )

        setSearchString string =
            ( model
                |> Tristate.mapMain
                    (PaginationSettings.setSearchStringAndReset
                        { searchStringLens =
                            Page.lenses.main.searchString
                        , paginationSettingsLens =
                            Page.lenses.main.pagination
                                |> Compose.lensWithLens Pagination.lenses.projects
                        }
                        string
                    )
            , Cmd.none
            )

        setProjectsPagination paginationSettings =
            ( model
                |> Tristate.mapMain ((Page.lenses.main.pagination |> Compose.lensWithLens Pagination.lenses.projects).set paginationSettings)
            , Cmd.none
            )

        setViewType viewType =
            ( model
                |> Tristate.mapMain (Page.lenses.main.viewType.set viewType)
            , Cmd.none
            )
    in
    case msg of
        Page.GotFetchDashboardAnalysisResponse result ->
            gotFetchDashboardAnalysisResponse result

        Page.SetProjectsPagination pagination ->
            setProjectsPagination pagination

        Page.SetSearchString string ->
            setSearchString string

        Page.SetViewType viewType ->
            setViewType viewType
