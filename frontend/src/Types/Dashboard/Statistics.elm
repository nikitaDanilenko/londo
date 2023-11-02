module Types.Dashboard.Statistics exposing (..)

import BigRational exposing (BigRational)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.DashboardStatistics
import Math.Natural
import Types.Dashboard.WithSimulation
import Types.Dashboard.WithoutSimulation


type alias Statistics =
    { reached : Types.Dashboard.WithSimulation.WithSimulation Math.Natural.Natural
    , reachable : Types.Dashboard.WithoutSimulation.WithoutSimulation
    , absoluteMeans : Types.Dashboard.WithSimulation.WithSimulation BigRational
    , relativeMeans : Types.Dashboard.WithSimulation.WithSimulation BigRational
    }


selection : SelectionSet Statistics LondoGQL.Object.DashboardStatistics
selection =
    SelectionSet.map4 Statistics
        (LondoGQL.Object.DashboardStatistics.reached Types.Dashboard.WithSimulation.selectionNatural)
        (LondoGQL.Object.DashboardStatistics.reachable Types.Dashboard.WithoutSimulation.selection)
        (LondoGQL.Object.DashboardStatistics.absoluteMeans Types.Dashboard.WithSimulation.selectionRational)
        (LondoGQL.Object.DashboardStatistics.relativeMeans Types.Dashboard.WithSimulation.selectionRational)
