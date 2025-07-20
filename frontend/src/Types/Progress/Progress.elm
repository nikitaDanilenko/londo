module Types.Progress.Progress exposing (..)

import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import List.Extra
import LondoGQL.Enum.TaskKind
import LondoGQL.Object
import LondoGQL.Object.Progress
import Math.Natural as Natural exposing (Natural)
import Math.Positive as Positive exposing (Positive)
import Maybe.Extra
import Monocle.Lens exposing (Lens)


type alias Progress =
    { reachable : Positive
    , reached : Natural
    }


lenses :
    { reachable : Lens Progress Positive
    , reached : Lens Progress Natural
    }
lenses =
    { reachable = Lens .reachable (\b a -> { a | reachable = b })
    , reached = Lens .reached (\b a -> { a | reached = b })
    }


isComplete : Progress -> Bool
isComplete progress =
    (progress.reached |> Natural.integerValue) == (progress.reachable |> Positive.integerValue)


selection : SelectionSet Progress LondoGQL.Object.Progress
selection =
    SelectionSet.map2
        Progress
        (LondoGQL.Object.Progress.reachable Positive.selection)
        (LondoGQL.Object.Progress.reached Natural.selection)


percentParts : Progress -> { whole : String, decimal : Maybe String }
percentParts progress =
    let
        numberOfDecimalPlaces =
            progress
                |> .reachable
                |> Positive.toString
                |> String.dropLeft 3
                |> String.length

        reachedString =
            progress |> .reached |> Natural.toString

        reachedStringLength =
            String.length reachedString
    in
    if numberOfDecimalPlaces <= 0 then
        { whole = reachedString
        , decimal = Nothing
        }

    else
        let
            ( before, after ) =
                reachedString |> String.toList |> List.Extra.splitAt (reachedStringLength - numberOfDecimalPlaces)
        in
        { whole = before |> String.fromList
        , decimal = after |> String.fromList |> Just
        }


displayPercentage : Progress -> String
displayPercentage progress =
    progress
        |> percentParts
        |> (\parts -> [ parts.whole |> Just, parts.decimal ])
        |> Maybe.Extra.values
        |> String.join "."


booleanToggle : Progress -> Progress
booleanToggle progress =
    let
        reached =
            if progress |> isComplete then
                Natural.zero

            else
                Natural.one
    in
    Progress Positive.one reached


default : LondoGQL.Enum.TaskKind.TaskKind -> Progress
default taskKind =
    case taskKind of
        LondoGQL.Enum.TaskKind.Discrete ->
            { reachable = Positive.one
            , reached = Natural.zero
            }

        LondoGQL.Enum.TaskKind.Percent ->
            { reachable = Positive.oneThousand
            , reached = Natural.zero
            }

        LondoGQL.Enum.TaskKind.Fraction ->
            { reachable = Positive.oneHundred
            , reached = Natural.zero
            }
