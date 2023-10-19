module Types.Simulation.Simulation exposing (..)

import BigInt exposing (BigInt)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import LondoGQL.Object
import LondoGQL.Object.Simulation
import Math.Constants
import Util.GraphQLUtil as GraphQLUtil


type alias Simulation =
    { reachedModifier : BigInt
    }


selection : SelectionSet Simulation LondoGQL.Object.Simulation
selection =
    SelectionSet.map
        (GraphQLUtil.bigIntFromGraphQL >> Maybe.withDefault Math.Constants.zeroBigInt >> Simulation)
        LondoGQL.Object.Simulation.reachedModifier
