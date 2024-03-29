module Types.Dashboard.WithSimulation exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.WithSimulationBigDecimal
import LondoGQL.Object.WithSimulationNatural
import LondoGQL.Scalar
import Math.Natural


type alias WithSimulation a =
    { total : a
    , counting : a
    , simulatedTotal : a
    , simulatedCounting : a
    }


selectionNatural : SelectionSet (WithSimulation Math.Natural.Natural) LondoGQL.Object.WithSimulationNatural
selectionNatural =
    SelectionSet.map4 WithSimulation
        (LondoGQL.Object.WithSimulationNatural.total Math.Natural.selection)
        (LondoGQL.Object.WithSimulationNatural.counting Math.Natural.selection)
        (LondoGQL.Object.WithSimulationNatural.simulatedTotal Math.Natural.selection)
        (LondoGQL.Object.WithSimulationNatural.simulatedCounting Math.Natural.selection)


selectionBigDecimal : SelectionSet (WithSimulation LondoGQL.Scalar.BigDecimal) LondoGQL.Object.WithSimulationBigDecimal
selectionBigDecimal =
    SelectionSet.map4 WithSimulation
        LondoGQL.Object.WithSimulationBigDecimal.total
        LondoGQL.Object.WithSimulationBigDecimal.counting
        LondoGQL.Object.WithSimulationBigDecimal.simulatedTotal
        LondoGQL.Object.WithSimulationBigDecimal.simulatedCounting
