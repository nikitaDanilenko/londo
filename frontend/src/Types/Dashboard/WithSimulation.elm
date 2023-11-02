module Types.Dashboard.WithSimulation exposing (..)

import BigRational exposing (BigRational)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.Rational
import LondoGQL.Object.WithSimulationNatural
import LondoGQL.Object.WithSimulationRational
import Math.Constants
import Math.Natural
import Math.Positive
import Util.GraphQLUtil as GraphQLUtil


type alias WithSimulation a =
    { total : a
    , counted : a
    , simulatedTotal : a
    , simulatedCounted : a
    }


selectionWith :
    { inner : SelectionSet a inner
    , total :
        SelectionSet a inner
        -> SelectionSet a gql
    , counted :
        SelectionSet a inner
        -> SelectionSet a gql
    , simulatedTotal : SelectionSet a inner -> SelectionSet a gql
    , simulatedCounted :
        SelectionSet a inner
        -> SelectionSet a gql
    }
    -> SelectionSet (WithSimulation a) gql
selectionWith ps =
    SelectionSet.map4 WithSimulation
        (ps.total ps.inner)
        (ps.counted ps.inner)
        (ps.simulatedTotal ps.inner)
        (ps.simulatedCounted ps.inner)


selectionNatural : SelectionSet (WithSimulation Math.Natural.Natural) LondoGQL.Object.WithSimulationNatural
selectionNatural =
    selectionWith
        { inner = Math.Natural.selection
        , total = LondoGQL.Object.WithSimulationNatural.total
        , counted = LondoGQL.Object.WithSimulationNatural.counted
        , simulatedTotal = LondoGQL.Object.WithSimulationNatural.simulatedTotal
        , simulatedCounted = LondoGQL.Object.WithSimulationNatural.simulatedCounted
        }


bigRationalSelection : SelectionSet BigRational LondoGQL.Object.Rational
bigRationalSelection =
    SelectionSet.map2 BigRational.fromBigInts
        (LondoGQL.Object.Rational.numerator |> SelectionSet.map (GraphQLUtil.bigIntFromGraphQL >> Maybe.withDefault Math.Constants.zeroBigInt))
        (LondoGQL.Object.Rational.denominator Math.Positive.selection |> SelectionSet.map Math.Positive.integerValue)


selectionRational : SelectionSet (WithSimulation BigRational) LondoGQL.Object.WithSimulationRational
selectionRational =
    selectionWith
        { inner = bigRationalSelection
        , total = LondoGQL.Object.WithSimulationRational.total
        , counted = LondoGQL.Object.WithSimulationRational.counted
        , simulatedTotal = LondoGQL.Object.WithSimulationRational.simulatedTotal
        , simulatedCounted = LondoGQL.Object.WithSimulationRational.simulatedCounted
        }
