module Util.ValidatedInput exposing
    ( ValidatedInput
    , boundedNatural
    , isValid
    , lenses
    , lift
    , maybeBoundedBigInt
    , natural
    , nonEmptyString
    , positive
    , set
    , updateBound
    )

import Basics.Extra exposing (flip)
import BigInt exposing (BigInt)
import Math.Natural as Natural exposing (Natural)
import Math.Positive as Positive exposing (Positive)
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Result.Extra


type alias ValidatedInput a =
    { value : a
    , ifEmptyValue : a
    , text : String
    , parse : String -> Result String a
    , partial : String -> Bool
    }


lenses :
    { text : Lens (ValidatedInput a) String
    , value : Lens (ValidatedInput a) a
    }
lenses =
    { text =
        Lens .text (\b a -> { a | text = b })
    , value =
        Lens .value (\b a -> { a | value = b })
    }


set : { value : a, toString : a -> String } -> ValidatedInput a -> ValidatedInput a
set ps input =
    input
        |> lenses.value.set ps.value
        |> lenses.text.set (ps.value |> ps.toString)


isValid : ValidatedInput a -> Bool
isValid fromInput =
    case fromInput.parse fromInput.text of
        Ok v ->
            v == fromInput.value

        Err _ ->
            False


setWithLens : Lens model (ValidatedInput a) -> String -> model -> model
setWithLens lens txt model =
    let
        validatedInput =
            lens.get model

        possiblyValid =
            if String.isEmpty txt || validatedInput.partial txt then
                validatedInput
                    |> lenses.text.set txt

            else
                validatedInput
    in
    case validatedInput.parse txt of
        Ok v ->
            possiblyValid
                |> lenses.value.set v
                |> flip lens.set model

        Err _ ->
            lens.set possiblyValid model


lift : Lens model (ValidatedInput a) -> Lens model String
lift lens =
    Lens (lens.get >> .text) (setWithLens lens)


natural : ValidatedInput Natural
natural =
    naturalWithParser Natural.fromString


boundedNatural : Natural -> ValidatedInput Natural
boundedNatural =
    boundedNaturalParser >> naturalWithParser


naturalWithParser : (String -> Result String Natural) -> ValidatedInput Natural
naturalWithParser parser =
    { value = Natural.zero
    , ifEmptyValue = Natural.zero
    , text = "0"
    , parse = parser
    , partial = parser >> Result.Extra.isOk
    }


updateBound : Natural -> ValidatedInput Natural -> ValidatedInput Natural
updateBound n input =
    let
        -- N.B.: We keep the original value. The original value may be larger than the new bound,
        -- but since all validated inputs should be checked, this decision allows a better UX,
        -- when updating the upper bound from, say, 15 to 16 with a reached value of 6. In that example there is an intermediate state,
        -- where the upper bound is 1, but we do not want to clamp the reached value to 1.
        currentValue =
            input.value
    in
    naturalWithParser (boundedNaturalParser n)
        |> lenses.value.set currentValue
        |> lenses.text.set (currentValue |> Natural.toString)


positive : ValidatedInput Positive
positive =
    let
        one =
            Positive.one

        parsePositive : String -> Result String Positive
        parsePositive str =
            (if String.isEmpty str then
                one
                    |> Positive.toString
                    |> Ok

             else if String.all Char.isDigit str && String.any ((/=) '0') str then
                Ok str

             else
                Err "The string does not represent a natural number"
            )
                |> Result.andThen
                    (String.toList
                        >> String.fromList
                        >> Positive.fromString
                        >> Result.fromMaybe "Not a positive number"
                    )
    in
    { value = one
    , ifEmptyValue = one
    , parse = parsePositive
    , text = "1"
    , partial =
        parsePositive
            >> Result.toMaybe
            >> Maybe.Extra.isJust
    }


boundedNaturalParser : Natural -> String -> Result String Natural
boundedNaturalParser n =
    Natural.fromString
        >> Result.Extra.filter
            ([ "The number is larger than the bound '", n |> Natural.toString, "'" ] |> String.concat)
            (\k -> Natural.min k n == k)


nonEmptyString : ValidatedInput String
nonEmptyString =
    { value = ""
    , ifEmptyValue = ""
    , text = ""
    , parse =
        Just
            >> Maybe.Extra.filter (String.isEmpty >> not)
            >> Result.fromMaybe "Error: Empty string"
    , partial = always True
    }


partialInt : String -> Bool
partialInt str =
    let
        tailCorrect =
            str |> String.dropLeft 1 |> String.all Char.isDigit

        headCorrect =
            str |> String.toList |> List.take 1 |> List.all (\c -> c == '-' || Char.isDigit c)
    in
    headCorrect && tailCorrect


maybeBoundedBigInt : { lower : BigInt, upper : BigInt } -> ValidatedInput (Maybe BigInt)
maybeBoundedBigInt bounds =
    { value = Nothing
    , ifEmptyValue = Nothing
    , text = ""
    , parse =
        \str ->
            if String.isEmpty str then
                Ok Nothing

            else
                str
                    |> BigInt.fromIntString
                    |> Maybe.Extra.filter (\int -> BigInt.gte int bounds.lower && BigInt.lte int bounds.upper)
                    |> Result.fromMaybe ("Error: Not an integer in the range between " ++ BigInt.toString bounds.lower ++ " and " ++ BigInt.toString bounds.upper)
                    |> Result.map Just
    , partial = partialInt
    }
