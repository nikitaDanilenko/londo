module Types.Dashboard.Statistics exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.DashboardStatistics
import LondoGQL.Scalar
import Math.Natural
import Types.Dashboard.Buckets
import Types.Dashboard.Tasks
import Types.Dashboard.WithSimulation
import Types.Dashboard.WithoutSimulation


type alias Statistics =
    { reached : Types.Dashboard.WithSimulation.WithSimulation Math.Natural.Natural
    , reachable : Types.Dashboard.WithoutSimulation.WithoutSimulation
    , absoluteMeans : Types.Dashboard.WithSimulation.WithSimulation LondoGQL.Scalar.BigDecimal
    , relativeMeans : Types.Dashboard.WithSimulation.WithSimulation LondoGQL.Scalar.BigDecimal
    , buckets : Types.Dashboard.Buckets.Buckets
    , tasks : Types.Dashboard.Tasks.Tasks
    , differenceTotalCounting : Int
    }


selection : SelectionSet Statistics LondoGQL.Object.DashboardStatistics
selection =
    SelectionSet.map7 Statistics
        (LondoGQL.Object.DashboardStatistics.reached Types.Dashboard.WithSimulation.selectionNatural)
        (LondoGQL.Object.DashboardStatistics.reachable Types.Dashboard.WithoutSimulation.selection)
        (LondoGQL.Object.DashboardStatistics.absoluteMeans Types.Dashboard.WithSimulation.selectionBigDecimal)
        (LondoGQL.Object.DashboardStatistics.relativeMeans Types.Dashboard.WithSimulation.selectionBigDecimal)
        (LondoGQL.Object.DashboardStatistics.buckets Types.Dashboard.Buckets.selection)
        (LondoGQL.Object.DashboardStatistics.tasks Types.Dashboard.Tasks.selection)
        LondoGQL.Object.DashboardStatistics.differenceTotalCounting
