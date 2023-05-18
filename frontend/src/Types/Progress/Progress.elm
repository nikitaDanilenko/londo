module Types.Progress.Progress exposing (..)

import Basics.Extra exposing (flip)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import List.Extra
import LondoGQL.Object
import LondoGQL.Object.Progress
import Math.Natural as Natural exposing (Natural)
import Math.Positive as Positive exposing (Positive)


type alias Progress =
    { reachable : Positive
    , reached : Natural
    }


isComplete : Progress -> Bool
isComplete progress =
    (progress.reached |> Natural.intValue) == (progress.reachable |> Positive.intValue)


selection : SelectionSet Progress LondoGQL.Object.Progress
selection =
    SelectionSet.map2
        Progress
        (LondoGQL.Object.Progress.reachable Positive.selection)
        (LondoGQL.Object.Progress.reached Natural.selection)


displayPercentage : Progress -> String
displayPercentage progress =
    let
        numberOfDecimalPlaces =
            progress
                |> .reachable
                |> Positive.intValue
                |> toFloat
                |> logBase 10
                |> flip (-) 2
                |> round

        reachedString =
            progress |> .reached |> Natural.toString

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
