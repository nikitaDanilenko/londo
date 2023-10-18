module Types.Simulation.Update exposing (..)

import LondoGQL.InputObject
import Monocle.Lens exposing (Lens)
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


initial : ClientInput
initial =
    { reachedModifier = ValidatedInput.maybeInt
    }


toGraphQLInput : ClientInput -> Maybe LondoGQL.InputObject.SimulationUpdate
toGraphQLInput input =
    input.reachedModifier
        |> .value
        |> Maybe.map LondoGQL.InputObject.SimulationUpdate
