module Types.Progress.Input exposing (..)

import LondoGQL.Enum.TaskKind
import LondoGQL.InputObject
import Math.Natural as Natural exposing (Natural)
import Math.Positive as Positive exposing (Positive)
import Monocle.Lens exposing (Lens)
import Types.Progress.Progress exposing (Progress)
import Util.ValidatedInput as ValidatedInput exposing (ValidatedInput)


type alias ClientInput =
    { reachable : ValidatedInput Positive
    , reached : ValidatedInput Natural
    }


progressOf : ClientInput -> Progress
progressOf input =
    Progress input.reachable.value input.reached.value


default : LondoGQL.Enum.TaskKind.TaskKind -> ClientInput
default taskKind =
    let
        max =
            case taskKind of
                LondoGQL.Enum.TaskKind.Discrete ->
                    Positive.one

                LondoGQL.Enum.TaskKind.Percent ->
                    Positive.tenToTheNth 3

                LondoGQL.Enum.TaskKind.Fraction ->
                    Positive.oneHundred
    in
    { reachable =
        ValidatedInput.positive
            |> ValidatedInput.set
                { value = max
                , toString = Positive.toString
                }
    , reached =
        ValidatedInput.boundedNatural (max |> Natural.fromPositive)
            |> ValidatedInput.set
                { value = Natural.zero
                , toString = Natural.toString
                }
    }


lenses :
    { reachable : Lens ClientInput (ValidatedInput Positive)
    , reached : Lens ClientInput (ValidatedInput Natural)
    }
lenses =
    { reachable = Lens .reachable (\b a -> { a | reachable = b })
    , reached = Lens .reached (\b a -> { a | reached = b })
    }


from : Progress -> ClientInput
from progress =
    { reachable =
        ValidatedInput.positive
            |> ValidatedInput.lenses.value.set progress.reachable
            |> ValidatedInput.lenses.text.set (progress.reachable |> Positive.toString)
    , reached =
        ValidatedInput.boundedNatural (progress.reachable |> Natural.fromPositive)
            |> ValidatedInput.lenses.value.set progress.reached
            |> ValidatedInput.lenses.text.set (progress.reached |> Natural.toString)
    }


toGraphQLInput : ClientInput -> LondoGQL.InputObject.ProgressInput
toGraphQLInput clientInput =
    { reached =
        clientInput.reached
            |> .value
            |> Natural.toGraphQLInput
    , reachable =
        clientInput.reachable
            |> .value
            |> Positive.toGraphQLInput
    }
