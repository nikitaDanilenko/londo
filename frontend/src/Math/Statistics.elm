module Math.Statistics exposing (..)

import Basics.Extra exposing (flip)
import BigInt exposing (BigInt)
import BigRational exposing (BigRational)
import Math.Constants
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
