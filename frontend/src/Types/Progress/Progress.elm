module Types.Progress.Progress exposing (..)

import BigInt
import BigRational exposing (BigRational)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import List.Extra
import LondoGQL.Object
import LondoGQL.Object.Progress
import Math.Constants as Constants
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



-- todo: Check usefulness


toRational : Progress -> BigRational
toRational progress =
    BigRational.fromBigInts
        (progress
            |> .reached
            |> Natural.integerValue
        )
        (progress
            |> .reachable
            |> Positive.integerValue
        )


toPercentRational : Progress -> BigRational
toPercentRational progress =
    BigRational.fromBigInts
        (progress
            |> .reached
            |> Natural.integerValue
            |> BigInt.mul Constants.oneHundredBigInt
        )
        (progress
            |> .reachable
            |> Positive.integerValue
        )


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


toDiscrete : Progress -> Progress
toDiscrete progress =
    { reached =
        if progress |> isComplete then
            Natural.one

        else
            Natural.zero
    , reachable = Positive.one
    }


toPercent : Progress -> Progress
toPercent progress =
    let
        approximation =
            approximatePercent progress

        decimalPlaces =
            approximation.decimal
                |> String.length

        reached =
            [ approximation.whole, approximation.decimal ]
                |> String.concat
                |> Natural.fromString
                |> Result.withDefault Natural.zero
    in
    { reachable = Positive.tenToTheNth (2 + decimalPlaces)
    , reached = reached
    }


approximatePercent : Progress -> { whole : String, decimal : String }
approximatePercent progress =
    let
        reachable =
            progress.reachable |> Positive.integerValue

        ( timesWhole, rem ) =
            BigInt.divmod (progress.reached |> Natural.integerValue |> BigInt.mul Constants.oneHundredBigInt) reachable
                |> Maybe.withDefault ( Constants.zeroBigInt, Constants.zeroBigInt )

        timesDecimal =
            BigInt.div (rem |> BigInt.mul (BigInt.pow Constants.tenBigInt Constants.tenBigInt)) reachable
    in
    { whole = timesWhole |> BigInt.toString
    , decimal =
        timesDecimal
            |> BigInt.toString
            |> String.toList
            |> List.Extra.dropWhileRight ((==) '0')
            |> String.fromList
    }
