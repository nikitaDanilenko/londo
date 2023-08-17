module Math.Statistics exposing (..)

import Basics.Extra exposing (flip)
import BigInt exposing (BigInt)
import BigRational exposing (BigRational)
import Math.Constants
import Math.Natural as Natural
import Math.Positive as Positive
import Maybe.Extra
import Types.Progress.Progress


sumWith : (a -> BigInt) -> List a -> BigInt
sumWith f =
    List.foldl (f >> BigInt.add) Math.Constants.zeroBigInt


bigRationalZero : BigRational
bigRationalZero =
    BigRational.fromInt 0


relativeExact : Int -> List Types.Progress.Progress.Progress -> BigRational
relativeExact divisor =
    List.foldl
        (Types.Progress.Progress.toPercentRational >> BigRational.add)
        bigRationalZero
        >> flip BigRational.div (divisor |> BigRational.fromInt)


relativeRounded : Int -> List Types.Progress.Progress.Progress -> BigRational
relativeRounded divisor =
    List.foldl
        (Types.Progress.Progress.toPercentRational
            >> BigRational.floor
            >> BigInt.add
        )
        Math.Constants.zeroBigInt
        >> BigRational.fromBigInt
        >> flip BigRational.div (divisor |> BigRational.fromInt)


{-| The difference if one additional reachable point is added is
100 / (n \* reachable)
where n is the number of all tasks.
-}
differenceAfterOneMoreExact : { numberOfElements : Int } -> Types.Progress.Progress.Progress -> Maybe BigRational
differenceAfterOneMoreExact ps =
    Just
        >> Maybe.Extra.filter (Types.Progress.Progress.isComplete >> not)
        >> Maybe.map
            (.reachable
                >> Positive.integerValue
                >> BigInt.mul (ps.numberOfElements |> BigInt.fromInt)
                >> BigRational.fromBigInts Math.Constants.oneHundredBigInt
            )


{-| The difference if one additional reachable point is added is
(1 / n) \* (floor(100 \* (1 + reached) / reachable) - floor(100 \* reached / reachable))
where n is the number of all tasks.
-}
differenceAfterOneMoreFloored : { numberOfElements : Int } -> Types.Progress.Progress.Progress -> Maybe BigRational
differenceAfterOneMoreFloored ps progress =
    let
        reached =
            progress.reached |> Natural.integerValue

        reachable =
            progress.reachable |> Positive.integerValue

        current =
            BigRational.fromBigInts
                (BigInt.mul
                    Math.Constants.oneHundredBigInt
                    reached
                )
                reachable

        assumed =
            BigRational.fromBigInts
                (BigInt.mul
                    Math.Constants.oneHundredBigInt
                    (BigInt.add
                        Math.Constants.oneBigInt
                        reached
                    )
                )
                reachable
    in
    progress
        |> Just
        |> Maybe.Extra.filter (Types.Progress.Progress.isComplete >> not)
        |> Maybe.map
            (\_ ->
                BigRational.fromBigInts
                    (BigInt.sub
                        (assumed |> BigRational.floor)
                        (current |> BigRational.floor)
                    )
                    (ps.numberOfElements |> BigInt.fromInt)
            )


differenceAfterCompletionExact : { numberOfElements : Int } -> Types.Progress.Progress.Progress -> Maybe BigRational
differenceAfterCompletionExact ps =
    Just
        >> Maybe.Extra.filter (Types.Progress.Progress.isComplete >> not)
        >> Maybe.map
            (Types.Progress.Progress.toRational
                >> BigRational.sub (BigRational.fromInt 1)
                >> BigRational.mul (BigRational.fromBigInts Math.Constants.oneHundredBigInt (ps.numberOfElements |> BigInt.fromInt))
            )
