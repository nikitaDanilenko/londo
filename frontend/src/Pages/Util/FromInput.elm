module Pages.Util.FromInput exposing
    ( FromInput
    , boundedNatural
    , emptyText
    , isValid
    , lift
    , limitTo
    , natural
    , percentualProgress
    , positive
    , text
    , value
    )

import Basics.Extra exposing (flip)
import Integer exposing (Integer)
import List.Extra
import LondoGQL.InputObject exposing (ProgressInput)
import LondoGQL.Scalar exposing (Natural(..), Positive(..))
import Maybe.Extra
import Monocle.Lens exposing (Lens)
import Pages.Util.MathUtil as MathUtil
import Pages.Util.ScalarUtil as ScalarUtil


type alias FromInput a =
    { value : a
    , ifEmptyValue : a
    , text : String
    , parse : String -> Result String a
    , partial : String -> Bool
    }


text : Lens (FromInput a) String
text =
    Lens .text (\b a -> { a | text = b })


value : Lens (FromInput a) a
value =
    Lens .value (\b a -> { a | value = b })


emptyText :
    { ifEmptyValue : a
    , value : a
    , parse : String -> Result String a
    , isPartial : String -> Bool
    }
    -> FromInput a
emptyText params =
    { value = params.value
    , ifEmptyValue = params.ifEmptyValue
    , text = ""
    , parse = params.parse
    , partial = params.isPartial
    }


isValid : FromInput a -> Bool
isValid fromInput =
    case fromInput.parse fromInput.text of
        Ok v ->
            v == fromInput.value

        Err _ ->
            False


setWithLens : Lens model (FromInput a) -> String -> model -> model
setWithLens lens txt model =
    let
        fromInput =
            lens.get model

        possiblyValid =
            if String.isEmpty txt || fromInput.partial txt then
                fromInput
                    |> text.set txt

            else
                fromInput
    in
    case fromInput.parse txt of
        Ok v ->
            possiblyValid
                |> value.set v
                |> flip lens.set model

        Err _ ->
            lens.set possiblyValid model


lift : Lens model (FromInput a) -> Lens model String
lift lens =
    Lens (lens.get >> .text) (setWithLens lens)


natural : FromInput Natural
natural =
    let
        zero =
            ScalarUtil.zeroNatural

        parseNatural : String -> Result String Natural
        parseNatural str =
            (if String.isEmpty str then
                zero
                    |> ScalarUtil.naturalToString
                    |> Ok

             else if String.all Char.isDigit str then
                Ok str

             else
                Err "The string does not represent a natural number"
            )
                |> Result.map
                    (String.toList
                        >> List.Extra.dropWhile ((==) '0')
                        >> String.fromList
                        >> Natural
                    )
    in
    emptyText
        { value = zero
        , ifEmptyValue = zero
        , parse = parseNatural
        , isPartial =
            parseNatural
                >> Result.toMaybe
                >> Maybe.Extra.isJust
        }


boundedNatural : Natural -> FromInput Natural
boundedNatural n =
    let
        zero =
            ScalarUtil.zeroNatural

        parseNatural =
            boundedNaturalParser n
    in
    emptyText
        { value = zero
        , ifEmptyValue = zero
        , parse = parseNatural
        , isPartial =
            parseNatural
                >> Result.toMaybe
                >> Maybe.Extra.isJust
        }


limitTo : Natural -> FromInput Natural -> FromInput Natural
limitTo n fi =
    { fi
        | parse = boundedNaturalParser n
        , value = ScalarUtil.minNatural fi.value n
    }


positive : FromInput Positive
positive =
    let
        one =
            Positive "1"

        parsePositive : String -> Result String Positive
        parsePositive str =
            (if String.isEmpty str then
                one
                    |> ScalarUtil.positiveToString
                    |> Ok

             else if String.all Char.isDigit str && String.any ((/=) '0') str then
                Ok str

             else
                Err "The string does not represent a natural number"
            )
                |> Result.map
                    (String.toList
                        >> List.Extra.dropWhile ((==) '0')
                        >> String.fromList
                        >> Positive
                    )
    in
    emptyText
        { value = one
        , ifEmptyValue = one
        , parse = parsePositive
        , isPartial =
            parsePositive
                >> Result.toMaybe
                >> Maybe.Extra.isJust
        }


percentualProgress : FromInput ProgressInput
percentualProgress =
    let
        zero =
            { reachable = ScalarUtil.positive100
            , reached = natural.ifEmptyValue
            }

        isDecimalPoint : Char -> Bool
        isDecimalPoint c =
            c == '.' || c == ','

        countDecimalPoints : String -> Int
        countDecimalPoints =
            String.toList >> List.Extra.count isDecimalPoint

        isPartial : String -> Bool
        isPartial =
            parseDecimal
                >> Result.toMaybe
                >> Maybe.Extra.isJust

        parseDecimal : String -> Result String ProgressInput
        parseDecimal txt =
            let
                decimalPoints =
                    countDecimalPoints txt

                withoutDecimalPoint =
                    txt |> String.filter (isDecimalPoint >> not)

                numberOfDecimalPlacesStrict =
                    MathUtil.numberOfDecimalPlaces txt

                numberOfDecimalPlacesFuzzy =
                    if numberOfDecimalPlacesStrict == 0 && decimalPoints == 1 then 1 else numberOfDecimalPlacesStrict
            in
            if decimalPoints <= 1 && ((String.length withoutDecimalPoint < 3 + numberOfDecimalPlacesStrict && String.all Char.isDigit withoutDecimalPoint) || txt == "100") then
                Ok
                    { reachable = "100" |> flip String.append (String.repeat numberOfDecimalPlacesFuzzy "0") |> Positive
                    , reached = Natural (String.append withoutDecimalPoint (String.repeat (if numberOfDecimalPlacesStrict == 0 && decimalPoints == 1 then 1 else 0) "0"))
                    }

            else
                Err "Not a valid percentual value"
    in
    emptyText
        { value = zero
        , ifEmptyValue = zero
        , parse = parseDecimal
        , isPartial = isPartial
        }


boundedNaturalParser : Natural -> String -> Result String Natural
boundedNaturalParser n =
    let
        zero =
            ScalarUtil.zeroNatural

        stringToInteger : String -> Integer
        stringToInteger =
            Integer.fromString >> Maybe.withDefault Integer.zero

        upperBound =
            n |> ScalarUtil.naturalToString |> stringToInteger

        parseNatural : String -> Result String Natural
        parseNatural str =
            (if String.isEmpty str then
                zero
                    |> ScalarUtil.naturalToString
                    |> Ok

             else if String.all Char.isDigit str && Integer.lte (stringToInteger str) upperBound then
                Ok str

             else
                Err "The string does not represent a natural number"
            )
                |> Result.map
                    (String.toList
                        >> List.Extra.dropWhile ((==) '0')
                        >> String.fromList
                        >> Natural
                    )
    in
    parseNatural
