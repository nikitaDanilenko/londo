module Types.Dashboard.Statistics exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.DashboardStatistics
import LondoGQL.Scalar
import Math.Natural
import Types.Dashboard.WithSimulation
import Types.Dashboard.WithoutSimulation


type alias Statistics =
    { reached : Types.Dashboard.WithSimulation.WithSimulation Math.Natural.Natural
    , reachable : Types.Dashboard.WithoutSimulation.WithoutSimulation
    , absoluteMeans : Types.Dashboard.WithSimulation.WithSimulation LondoGQL.Scalar.BigDecimal
    , relativeMeans : Types.Dashboard.WithSimulation.WithSimulation LondoGQL.Scalar.BigDecimal
    }


selection : SelectionSet Statistics LondoGQL.Object.DashboardStatistics
selection =
    SelectionSet.map4 Statistics
        (LondoGQL.Object.DashboardStatistics.reached Types.Dashboard.WithSimulation.selectionNatural)
        (LondoGQL.Object.DashboardStatistics.reachable Types.Dashboard.WithoutSimulation.selection)
        (LondoGQL.Object.DashboardStatistics.absoluteMeans Types.Dashboard.WithSimulation.selectionBigDecimal)
        (LondoGQL.Object.DashboardStatistics.relativeMeans Types.Dashboard.WithSimulation.selectionBigDecimal)
