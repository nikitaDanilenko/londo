module Types.Simulation.Update exposing (..)

import LondoGQL.InputObject
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Types.Simulation.Simulation
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { reachedModifier : ValidatedInput (Maybe Int)
    }


lenses :
    { reachedModifier : Lens ClientInput (ValidatedInput (Maybe Int))
    }
lenses =
    { reachedModifier = Lens .reachedModifier (\b a -> { a | reachedModifier = b })
    }


from : Maybe Types.Simulation.Simulation.Simulation -> ClientInput
from simulation =
    let
        maybeModifier =
            simulation |> Maybe.map .reachedModifier
    in
    { reachedModifier =
        ValidatedInput.set
            { value = maybeModifier
            , toString = Maybe.Extra.unwrap "" String.fromInt
            }
            ValidatedInput.maybeInt
    }


toGraphQLInput : ClientInput -> Maybe LondoGQL.InputObject.SimulationUpdate
toGraphQLInput input =
    input.reachedModifier
        |> .value
        |> Maybe.map LondoGQL.InputObject.SimulationUpdate
