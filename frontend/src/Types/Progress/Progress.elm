module Types.Progress.Progress exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import List.Extra
import LondoGQL.Enum.TaskKind as TaskKind exposing (TaskKind)
import LondoGQL.Object
import LondoGQL.Object.Progress
import Math.Natural as Natural exposing (Natural)
import Math.Positive as Positive exposing (Positive)


type alias Progress =
    { reachable : Positive
    , reached : Natural
    }


selection : SelectionSet Progress LondoGQL.Object.Progress
selection =
    SelectionSet.map2
        Progress
        (LondoGQL.Object.Progress.reachable Positive.selection)
        (LondoGQL.Object.Progress.reached Natural.selection)


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
