module Types.Task.Progress exposing (..)

import List.Extra
import LondoGQL.Enum.TaskKind as TaskKind exposing (TaskKind)
import LondoGQL.InputObject
import Monocle.Lens exposing (Lens)
import Pages.Util.ValidatedInput exposing (ValidatedInput)
import Types.Task.Natural as Natural exposing (Natural)
import Types.Task.Positive as Positive exposing (Positive)


type alias Progress =
    { reachable : Positive
    , reached : Natural
    }


type alias ClientInput =
    { reachable : ValidatedInput Positive
    , reached : ValidatedInput Natural
    }


lenses :
    { reachable : Lens Progress Positive
    , reached : Lens Progress Natural
    }
lenses =
    { reachable =
        Lens .reachable (\b a -> { a | reachable = b })
    , reached =
        Lens .reached (\b a -> { a | reached = b })
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


display : TaskKind -> Progress -> String
display tk p =
    case tk of
        TaskKind.Percentual ->
            let
                numberOfDecimalPlaces =
                    logBase 10 (toFloat p.reachable.positive) - 2 |> round

                reachedString =
                    p.reached |> Natural.toString

                reachedStringLength =
                    String.length reachedString

                percent =
                    if numberOfDecimalPlaces <= 0 then
                        reachedString

                    else
                        let
                            ( before, after ) =
                                reachedString |> String.toList |> List.Extra.splitAt (reachedStringLength - numberOfDecimalPlaces)
                        in
                        String.concat [ String.fromList before, ".", String.fromList after ]
            in
            String.concat [ percent, "%" ]

        _ ->
            String.concat [ Natural.toString p.reached, "/", Positive.toString p.reachable ]
