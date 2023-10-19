module Types.Simulation.Update exposing (..)

import BigInt exposing (BigInt)
import LondoGQL.InputObject
import Math.Natural
import Math.Positive
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Types.Progress.Progress
import Types.Simulation.Simulation
import Util.GraphQLUtil as GraphQLUtil
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { reachedModifier : ValidatedInput (Maybe BigInt)
    }


lenses :
    { reachedModifier : Lens ClientInput (ValidatedInput (Maybe BigInt))
    }
lenses =
    { reachedModifier = Lens .reachedModifier (\b a -> { a | reachedModifier = b })
    }


from :
    { progress : Types.Progress.Progress.Progress
    , simulation : Maybe Types.Simulation.Simulation.Simulation
    }
    -> ClientInput
from ps =
    let
        maybeModifier =
            ps |> .simulation |> Maybe.map .reachedModifier
    in
    { reachedModifier =
        ValidatedInput.set
            { value = maybeModifier
            , toString = Maybe.Extra.unwrap "" BigInt.toString
            }
        <|
            ValidatedInput.maybeBoundedBigInt
                { lower = ps |> .progress |> .reached |> Math.Natural.integerValue |> BigInt.negate
                , upper = ps |> .progress |> (\p -> BigInt.sub (p.reachable |> Math.Positive.integerValue) (p.reached |> Math.Natural.integerValue))
                }
    }


toGraphQLInput : ClientInput -> Maybe LondoGQL.InputObject.SimulationUpdate
toGraphQLInput input =
    input.reachedModifier
        |> .value
        |> Maybe.map (GraphQLUtil.bigIntToGraphQL >> LondoGQL.InputObject.SimulationUpdate)
