module Crypto.Arithmetic exposing (powerMod)

import Integer exposing (Integer)
import Maybe.Extra exposing (unwrap)


type alias PowerMod =
    { base : Integer, exponent : Integer, modulus : Integer }


isOddInteger : Integer -> Bool
isOddInteger =
    isEvenInteger >> not


isEvenInteger : Integer -> Bool
isEvenInteger n =
    unwrap False (\x -> Integer.zero == x) (modByInteger n two)


two : Integer
two =
    Integer.fromInt 2


modByInteger : Integer -> Integer -> Maybe Integer
modByInteger dividend divisor =
    Maybe.map Tuple.second (Integer.divmod dividend divisor)


{-| An efficient computation of modular power.
The optional result should only ever occur, when the chosen modulus is zero.
This implementation is strongly based on the one in lynn/elm-arithmetic.
-}
powerMod : PowerMod -> Maybe Integer
powerMod p =
    let
        go : { base : Integer, exponent : Integer, result : Integer } -> Maybe Integer
        go q =
            if q.exponent == Integer.zero then
                Just q.result

            else
                let
                    nextResult =
                        if isOddInteger q.exponent then
                            modByInteger (Integer.mul q.result q.base) p.modulus

                        else
                            Just q.result
                in
                modByInteger (Integer.mul q.base q.base) p.modulus
                    |> Maybe.andThen
                        (\nextBase ->
                            Integer.div q.exponent two
                                |> Maybe.andThen
                                    (\nextExponent ->
                                        nextResult
                                            |> Maybe.andThen
                                                (\x ->
                                                    go
                                                        { base = nextBase
                                                        , exponent = nextExponent
                                                        , result = x
                                                        }
                                                )
                                    )
                        )
    in
    if p.modulus == Integer.one then
        Just Integer.zero

    else if p.modulus == Integer.zero then
        Nothing

    else
        modByInteger p.base p.modulus
            |> Maybe.andThen
                (\base ->
                    go
                        { base = base
                        , exponent = p.exponent
                        , result = Integer.one
                        }
                )
