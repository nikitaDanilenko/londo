module Math.Statistics exposing (..)

import Basics.Extra exposing (flip)
import BigInt exposing (BigInt)
import BigRational exposing (BigRational)
import Math.Constants
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


differenceAfterCompletionExact : { numberOfElements : Int } -> Types.Progress.Progress.Progress -> Maybe BigRational
differenceAfterCompletionExact ps =
    Just
        >> Maybe.Extra.filter (Types.Progress.Progress.isComplete >> not)
        >> Maybe.map
            (Types.Progress.Progress.toRational
                >> flip BigRational.sub (BigRational.fromInt 1)
                >> BigRational.mul (BigRational.fromBigInts Math.Constants.oneHundredBigInt (ps.numberOfElements |> BigInt.fromInt))
            )
